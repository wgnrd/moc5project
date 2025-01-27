package com.example.canteenchecker.adminapp.core;

import java.util.Date;

public class ReviewData {
  private final int rating;
  private final String remark;
  private final String id;
  private final String creator;
  private final Date creationDate;

  public ReviewData(int rating, String remark, String id, String creator, Date creationDate) {
    this.rating = rating;
    this.remark = remark;
    this.creationDate = creationDate;
    this.creator = creator;
    this.id = id;
  }

  public Date getCreationDate() {
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

