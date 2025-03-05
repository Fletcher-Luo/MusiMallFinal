package com.ByteAndHeartDance.auth.service.auth;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.ByteAndHeartDance.auth.entity.auth.UserConditionEntity;
import com.ByteAndHeartDance.auth.entity.auth.UserEntity;
import com.ByteAndHeartDance.auth.entity.auth.UserRoleEntity;
import com.ByteAndHeartDance.auth.mapper.auth.UserAvatarMapper;
import com.ByteAndHeartDance.auth.mapper.auth.UserMapper;
import com.ByteAndHeartDance.auth.entity.auth.UserAvatarEntity;
import com.ByteAndHeartDance.auth.mapper.auth.UserRoleMapper;
import com.ByteAndHeartDance.auth.utils.PasswordUtil;
import com.ByteAndHeartDance.entity.auth.AuthUserEntity;
import com.ByteAndHeartDance.entity.auth.CaptchaEntity;
import com.ByteAndHeartDance.entity.auth.JwtUserEntity;
import com.ByteAndHeartDance.entity.auth.TokenEntity;
import com.ByteAndHeartDance.entity.auth.UserWebEntity;
import com.ByteAndHeartDance.exception.BusinessException;
import com.ByteAndHeartDance.helper.IdGenerateHelper;
import com.ByteAndHeartDance.helper.TokenHelper;
import com.ByteAndHeartDance.mapper.BaseMapper;
import com.ByteAndHeartDance.service.BaseService;
import com.ByteAndHeartDance.utils.AssertUtil;
import com.ByteAndHeartDance.utils.FillUserUtil;
import com.ByteAndHeartDance.utils.RedisUtil;
import com.ByteAndHeartDance.utils.TokenUtil;
import com.alibaba.fastjson.JSON;
import com.wf.captcha.ArithmeticCaptcha;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ByteAndHeartDance.auth.utils.CaptchaKeyUtil.getCaptchaKey;
import static com.ByteAndHeartDance.utils.AssertUtil.ASSERT_ERROR_CODE;


/**
 * 用户 服务层
 */
@Slf4j
@Service
public class UserService extends BaseService<UserEntity, UserConditionEntity> {

	private UserMapper userMapper;

	private RedisUtil redisUtil;

	private UserRoleMapper userRoleMapper;

	private PasswordUtil passwordUtil;

	private UserAvatarMapper userAvatarMapper;

	private TokenHelper tokenHelper;

	private IdGenerateHelper idGenerateHelper;

	private AuthenticationManager authenticationManager;

	private UserDetailsService userDetailsService;

	private static final String DEFAULT_PASSWORD = "123456";
	private static final String REGISTER_USER_PREFIX = "registerUser:";
	@Value("${mall.mgt.tokenExpireTimeInRecord:3600}")
	private int tokenExpireTimeInRecord;
	@Value("${mall.mgt.captchaExpireSecond:60}")
	private int captchaExpireSecond;

	public UserService(UserMapper userMapper, AuthenticationManager authenticationManager, UserRoleMapper userRoleMapper, RedisUtil redisUtil, PasswordUtil passwordUtil, IdGenerateHelper idGenerateHelper, UserAvatarMapper userAvatarMapper, TokenHelper tokenHelper,UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
		this.authenticationManager = authenticationManager;
		this.idGenerateHelper = idGenerateHelper;
		this.userRoleMapper = userRoleMapper;
		this.userAvatarMapper = userAvatarMapper;
		this.tokenHelper = tokenHelper;
		this.userMapper = userMapper;
		this.redisUtil = redisUtil;
		this.passwordUtil = passwordUtil;
	}

	/**
	 * 查询用户信息
	 *
	 * @param id 用户ID
	 * @return 用户信息
	 */
	public UserEntity findById(Long id) {
		return userMapper.findById(id);
	}

	@Override
	protected BaseMapper getBaseMapper() {
		return userMapper;
	}

	@Transactional(rollbackFor = Throwable.class)
	public void insert(UserEntity userEntity) {
		UserConditionEntity userConditionEntity = new UserConditionEntity();
		userConditionEntity.setUserName(userEntity.getUserName());
		AssertUtil.isTrue(CollectionUtils.isEmpty(userMapper.searchByCondition(userConditionEntity)), "用户名称已存在");

		userConditionEntity = new UserConditionEntity();
		userConditionEntity.setEmail(userEntity.getEmail());
		AssertUtil.isTrue(CollectionUtils.isEmpty(userMapper.searchByCondition(userConditionEntity)), "邮箱已存在");
		if (!StringUtils.hasLength(userEntity.getPassword())) {
			userEntity.setPassword(DEFAULT_PASSWORD);
		}
		userEntity.setPassword(passwordUtil.encode(userEntity.getPassword()));
		fillData(userEntity);
		userMapper.insert(userEntity);
		userEntity.setId( userMapper.findByUserName(userEntity.getUserName()).getId());
		userRoleMapper.deleteByUserId(userEntity.getId());
		List<UserRoleEntity> userRoleEntities = buildUserRoleEntityList(userEntity);
		if (CollectionUtils.isNotEmpty(userRoleEntities)) {
			userRoleMapper.batchInsert(userRoleEntities);
		}
		saveUserToRedis(userEntity);
	}


	/**
	 * 修改用户
	 *
	 * @param userEntity 用户信息
	 * @return 结果
	 */
	@Transactional(rollbackFor = Throwable.class)
	public int update(UserEntity userEntity) {
		return userMapper.update(userEntity);
	}

	public int resetPwd(List<Long> ids) {
		List<UserEntity> userEntities = userMapper.findByIds(ids);
		AssertUtil.notEmpty(userEntities, "用户不存在");

		for (UserEntity userEntity : userEntities) {
			userEntity.setPassword(passwordUtil.encode(DEFAULT_PASSWORD));
			FillUserUtil.fillUpdateUserInfo(userEntity);
		}
		return userMapper.updateForBatch(userEntities);
	}


	/**
	 * 用户登录
	 *
	 * @param authUserVo
	 * @return
	 */
	public TokenEntity login(AuthUserEntity authUserVo) {
		String code = redisUtil.get(getCaptchaKey(authUserVo.getUuid()));
		AssertUtil.hasLength(code, "该验证码已失效");
		AssertUtil.isTrue(code.trim().equals(authUserVo.getCode().trim()), "验证码错误");
		try {
			UserEntity userEntity = userMapper.findByUserName(authUserVo.getUsername());
			AssertUtil.notNull(userEntity, "该用户不存在");
			if (!passwordUtil.matches(authUserVo.getPassword(), userEntity.getPassword())) {
				throw new BusinessException(ASSERT_ERROR_CODE, "用户名或密码错误");
			}
			UsernamePasswordAuthenticationToken authenticationToken =
					new UsernamePasswordAuthenticationToken(authUserVo.getUsername(), authUserVo.getPassword());
			Authentication authentication = authenticationManager.authenticate(authenticationToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			JwtUserEntity jwtUserEntity = (JwtUserEntity) (authentication.getPrincipal());
			String token = tokenHelper.generateToken((JwtUserEntity) (authentication.getPrincipal()));
			redisUtil.del(getCaptchaKey(authUserVo.getUuid()));
			List<String> roles = jwtUserEntity.getAuthorities().stream().map(SimpleGrantedAuthority::getAuthority).collect(Collectors.toList());
			return new TokenEntity(jwtUserEntity.getUsername(), token, roles, tokenExpireTimeInRecord);
		} catch (Exception e) {
			log.info("登录失败：", e);
			if (e instanceof BusinessException) {
				throw e;
			}
			throw new BusinessException(ASSERT_ERROR_CODE, "用户名或密码错误");
		}

	}

	/**
	 * 获取当前登录的用户详情
	 *
	 * @return 用户详情
	 */
	public UserWebEntity getUserDetail() {
		UserWebEntity userWebEntity = new UserWebEntity();
		String currentUsername = tokenHelper.getCurrentUsername();

		String jsonData = redisUtil.get(getUserKey(currentUsername));
		UserEntity userEntity = JSONUtil.toBean(jsonData, UserEntity.class);
		if (Objects.isNull(userEntity)) {
			return userWebEntity;
		}

		userWebEntity.setId(userEntity.getId());
		userWebEntity.setUserName(userEntity.getUserName());
		userWebEntity.setNickName(userEntity.getNickName());
		userWebEntity.setSex(userEntity.getSex());
		userWebEntity.setEmail(userEntity.getEmail());

		UserAvatarEntity userAvatarEntity = userAvatarMapper.findById(userEntity.getId());
		if (Objects.nonNull(userAvatarEntity)) {
			userWebEntity.setAvatarUrl(userAvatarEntity.getPath());
		}
		return userWebEntity;
	}

	/**
	 * 获取当前登录的用户信息
	 *
	 * @return 用户信息
	 */
	public JwtUserEntity getUserInfo() {
		String currentUsername = tokenHelper.getCurrentUsername();
		return (JwtUserEntity) userDetailsService.loadUserByUsername(currentUsername);
	}


	private void fillData(UserEntity userEntity) {
		if (Objects.nonNull(userEntity.getDept())) {
			userEntity.setDeptId(userEntity.getDept().getId());
		}

		if (CollectionUtils.isNotEmpty(userEntity.getJobs())) {
			userEntity.setJobId(userEntity.getJobs().get(0).getId());
		}
	}

	public void logout(HttpServletRequest request) {
		String token = TokenUtil.getTokenForAuthorization(request);
		AssertUtil.hasLength(token, "请重新登录");
		tokenHelper.delToken(token);
	}

	private List<UserRoleEntity> buildUserRoleEntityList(UserEntity userEntity) {
		if (CollectionUtils.isNotEmpty(userEntity.getRoles())) {
			return userEntity.getRoles().stream().map(x -> {
				UserRoleEntity userRoleEntity = new UserRoleEntity();
				userRoleEntity.setId(idGenerateHelper.nextId());
				userRoleEntity.setUserId(userEntity.getId());
				userRoleEntity.setRoleId(x.getId());
				return userRoleEntity;
			}).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private void saveUserToRedis(UserEntity userEntity) {
		redisUtil.setIfAbsent(getUserKey(userEntity.getUserName()), JSON.toJSONString(userEntity));
	}

	public CaptchaEntity getCode() {
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(111, 36);
		// 几位数运算，默认是两位
		captcha.setLen(2);
		// 获取运算的结果
		String result = "";
		try {
			result = new BigDecimal(captcha.text()).intValue() + "";
		} catch (Exception e) {
			result = captcha.text();
		}
		String uuid = "C" + IdUtil.simpleUUID();
		// 保存验证码到Redis中
		redisUtil.set(getCaptchaKey(uuid), result, captchaExpireSecond);
		return new CaptchaEntity(uuid, captcha.toBase64());
	}

	private String getUserKey(String userName) {
		return String.format("%s%s", REGISTER_USER_PREFIX, userName);
	}

	private String getUserDetailsKey(String userName) {
		return String.format("%s%s", "user:", userName);
	}
}
