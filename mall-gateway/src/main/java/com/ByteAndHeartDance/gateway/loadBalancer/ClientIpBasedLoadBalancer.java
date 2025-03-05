package com.ByteAndHeartDance.gateway.loadBalancer;

import cn.hutool.core.net.NetUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.core.NoopServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ReactorServiceInstanceLoadBalancer;
import org.springframework.cloud.loadbalancer.core.SelectedInstanceCallback;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;

import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

//@Component
// 通过config来加载这个bean
@Slf4j
public class ClientIpBasedLoadBalancer implements ReactorServiceInstanceLoadBalancer {

  /**
   * 说明：本策略是基于本机IP地址的负载均衡策略，即优先选择与本机IP地址一样的服务实例。以便多个开发者在本地调试时，可以优先选择本地服务实例。防止走串
   * 参考：https://blog.51cto.com/huangrd/9717178
   * 如需关闭本策略，只需在LoadBalancerConfig中的注释打开即可。
   */
  final AtomicInteger position;
  final String serviceId;

  ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider;
  private final NacosDiscoveryProperties nacosDiscoveryProperties;
  private final Set<String> localIps;

  public ClientIpBasedLoadBalancer(ObjectProvider<ServiceInstanceListSupplier> serviceInstanceListSupplierProvider, String serviceId, NacosDiscoveryProperties nacosDiscoveryProperties) {
    this.serviceId = serviceId;
    this.serviceInstanceListSupplierProvider = serviceInstanceListSupplierProvider;
    this.nacosDiscoveryProperties = nacosDiscoveryProperties;
    this.localIps = NetUtil.localIpv4s();
    this.position = new AtomicInteger(new Random().nextInt(1000));
  }

  @Override
  public Mono<Response<ServiceInstance>> choose(Request request) {
// 获取 ServiceInstanceListSupplier
    ServiceInstanceListSupplier supplier = serviceInstanceListSupplierProvider
            .getIfAvailable(NoopServiceInstanceListSupplier::new);
    return supplier.get(request).next()
            .map(serviceInstances -> processInstanceResponse(supplier, serviceInstances));

  }



  private Response<ServiceInstance> processInstanceResponse(ServiceInstanceListSupplier supplier,
                                                            List<ServiceInstance> serviceInstances) {
    Response<ServiceInstance> serviceInstanceResponse = getInstanceResponse(serviceInstances);
    if (supplier instanceof SelectedInstanceCallback && serviceInstanceResponse.hasServer()) {
      ((SelectedInstanceCallback) supplier).selectedServiceInstance(serviceInstanceResponse.getServer());
    }
    return serviceInstanceResponse;
  }

  private Response<ServiceInstance> getInstanceResponse(List<ServiceInstance> serviceInstances) {
    if (serviceInstances.isEmpty()) {
      if (log.isWarnEnabled()) {
        log.warn("No servers available for service: ");
      }
      return new EmptyResponse();
    }
    // 过滤与本机IP地址一样的服务实例
    if (!CollectionUtils.isEmpty(this.localIps)) {
      for (ServiceInstance instance : serviceInstances) {
        String host = instance.getHost();
        if (this.localIps.contains(host)) {
          return new DefaultResponse(instance);
        }
      }
    }
    // 随机选择一个服务实例
    int index = new Random().nextInt(serviceInstances.size());
    return new DefaultResponse(serviceInstances.get(index));
  }
}
