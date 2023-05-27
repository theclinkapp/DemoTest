package com.myclinkapp.demotest.realm;


import org.bson.types.ObjectId;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmField;

public class colleges extends RealmObject {


    @PrimaryKey
    private ObjectId _id;
    private Date addOn;
    private address address;
    private String collegeCode;

    private RealmList<courseList> courseList;
    private String email;
    private String fullAddress;
    private String fullName;
    private String image;
    private String nameSearch;
    private ObjectId owner_id;
    private String shortName;
    private String status;
    private Date timeStamp;
    private String universityCode;
    private String website;
    private String contact;


    public colleges() {
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }



    public Date getAddOn() {
        return addOn;
    }

    public void setAddOn(Date addOn) {
        this.addOn = addOn;
    }

    public com.myclinkapp.demotest.realm.address getAddress() {
        return address;
    }

    public void setAddress(com.myclinkapp.demotest.realm.address address) {
        this.address = address;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }


    public RealmList<com.myclinkapp.demotest.realm.courseList> getCourseList() {
        return courseList;
    }

    public void setCourseList(RealmList<com.myclinkapp.demotest.realm.courseList> courseList) {
        this.courseList = courseList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNameSearch() {
        return nameSearch;
    }

    public void setNameSearch(String nameSearch) {
        this.nameSearch = nameSearch;
    }

    public ObjectId getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(ObjectId owner_id) {
        this.owner_id = owner_id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUniversityCode() {
        return universityCode;
    }

    public void setUniversityCode(String universityCode) {
        this.universityCode = universityCode;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}


