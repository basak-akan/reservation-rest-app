package com.tot.codechallenge.util;

import org.springframework.data.domain.Sort;

public class ControllerUtil {

  public static Sort parseSortParameter(String[] sort) {
    Sort result = Sort.unsorted();
    if (sort.length > 0 && sort[0].contains(",")) {
      String[] sortParams = sort[0].split(",");
      if(sortParams.length > 1) {
        result = Sort.by(Sort.Direction.fromString(sortParams[1].trim()), sortParams[0].trim());
      }
    }
    return result;
  }
}
