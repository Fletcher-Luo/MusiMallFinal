package com.ByteAndHeartDance.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
public class ResponsePage<T> implements Serializable {

  /**
   * 页码
   */
  private Integer pageNo;

  /**
   * 每页大小
   */
  private Integer pageSize;

  /**
   * 总页数
   */
  private Integer totalPage;


  /**
   * 总记录数
   */
  private Integer totalCount;

  /**
   * 数据
   */
  private List<T> data;

}

