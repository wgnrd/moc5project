package com.example.canteenchecker.adminapp.core;

import java.util.Date;

public class ReviewData {
  private final int rating;
  private final String remark;
  private final String id;
  private final String creator;
  private final String creationDate;

  public ReviewData(int rating, String remark, String id, String creator, String creationDate) {
    this.rating = rating;
    this.remark = remark;
    this.creationDate = creationDate;
    this.creator = creator;
    this.id = id;
  }

  public String getCreationDate() {
    return creationDate;
  }

  public String getCreator() {
    return creator;
  }

  public String getId() {
    return id;
  }

  public String getRemark() {
    return remark;
  }

  public int getRating() {
    return rating;
  }
}

