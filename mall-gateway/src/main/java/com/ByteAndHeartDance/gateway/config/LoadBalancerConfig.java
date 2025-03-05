package com.ByteAndHeartDance.gateway.config;

import com.ByteAndHeartDance.gateway.loadBalancer.ClientIpBasedLoadBalancer;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class LoadBalancerConfig {

  /**
   * 本地优先策略
   *
   * @param environment 环境变量
   * @param loadBalancerClientFactory 工厂
   * @param nacosDiscoveryProperties 属性
   * @return ReactorLoadBalancer
   */
  @Bean
  public ReactorLoadBalancer<ServiceInstance> nacosLocalFirstLoadBalancer(
      Environment environment,
      LoadBalancerClientFactory loadBalancerClientFactory,
      NacosDiscoveryProperties nacosDiscoveryProperties) {
    String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
    ObjectProvider<ServiceInstanceListSupplier> lazyProvider =
        loadBalancerClientFactory.getLazyProvider(name, ServiceInstanceListSupplier.class);

    return new ClientIpBasedLoadBalancer(lazyProvider, name, nacosDiscoveryProperties);
    // 使用随机，请打开下面的注释，并注释掉上面的return
    // return new RoundRobinLoadBalancer(lazyProvider, name);
  }
}
