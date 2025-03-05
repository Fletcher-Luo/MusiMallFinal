package com.ByteAndHeartDance.mybatis;


import com.ByteAndHeartDance.entity.auth.JwtUserEntity;
import com.ByteAndHeartDance.helper.IdGenerateHelper;
import com.ByteAndHeartDance.utils.FillUserUtil;
import com.ByteAndHeartDance.utils.SpringUtil;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.xmltags.DynamicContext;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.SqlNode;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;


@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
/**
 * UserInterceptor主要作用是将当前用户信息设置到CURRENT_USER_ID、CURRENT_USER_NAME中，同时设置雪花算法生成的ID的值到GENERATE_ID中。
 */
public class UserInterceptor implements Interceptor {

    private static final String CURRENT_USER_ID = "CURRENT_USER_ID";
    private static final String CURRENT_USER_NAME = "CURRENT_USER_NAME";
    private static final String GENERATE_ID = "GENERATE_ID";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();

        if (target instanceof Executor) {
            MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
            Collection<MappedStatement> mappedStatements = mappedStatement.getConfiguration().getMappedStatements();
            if (mappedStatements.isEmpty()) {
                return invocation.proceed();
            }
            Iterator<MappedStatement> iterator = mappedStatements.iterator();
            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (object instanceof MappedStatement) {
                    MappedStatement objectMappedStatement = (MappedStatement) object;
                    if (!objectMappedStatement.getId().equals(mappedStatement.getId())) {
                        continue;
                    }
                    SqlSource sqlSource = objectMappedStatement.getSqlSource();
                    Field field;
                    if (sqlSource instanceof DynamicSqlSource) {
                        field = DynamicSqlSource.class.getDeclaredField("rootSqlNode");
                        field.setAccessible(true);
                        SqlNode rootSqlNode = (SqlNode) field.get(sqlSource);
                        if (!(rootSqlNode instanceof Proxy)) {
                            boolean isInsert = SqlCommandType.INSERT.equals(mappedStatement.getSqlCommandType());
                            SqlNode proxySqlNode = (SqlNode) Proxy.newProxyInstance(rootSqlNode.getClass().getClassLoader(),
                                    new Class[]{SqlNode.class},
                                    new CustomizeInvocationHandler(rootSqlNode, isInsert));
                            field.set(sqlSource, proxySqlNode);
                        }
                    }
                }

            }
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    private class CustomizeInvocationHandler implements InvocationHandler {
        private final SqlNode target;
        private boolean isInsert;

        CustomizeInvocationHandler(SqlNode target, boolean isInsert) {
            this.target = target;
            this.isInsert = isInsert;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            DynamicContext context = (DynamicContext) args[0];
            JwtUserEntity currentUserInfo = FillUserUtil.getCurrentUserInfoOrNull();
            if (!Objects.nonNull(currentUserInfo)) {
                context.bind(CURRENT_USER_ID, 1);
                context.bind(CURRENT_USER_NAME, "admin");
            }else{
                context.bind(CURRENT_USER_ID, currentUserInfo.getId());
                context.bind(CURRENT_USER_NAME, currentUserInfo.getUsername());
            }


            if (isInsert) {
                IdGenerateHelper idGenerateHelper = SpringUtil.getBean(IdGenerateHelper.class);
                context.bind(GENERATE_ID, idGenerateHelper.nextId());
            }

            return method.invoke(target, args);
        }
    }
}
