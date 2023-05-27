package com.myclinkapp.demotest.realm;

import org.bson.types.ObjectId;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;


@RealmClass(embedded=true)
public class courseList extends RealmObject {

    private String _id;
    private String owner_Id;
    private String country;
    private String courseCode;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOwner_Id() {
        return owner_Id;
    }

    public void setOwner_Id(String owner_Id) {
        this.owner_Id = owner_Id;
    }

    private String depCode;
    private String depName;
    private String depShortName;
    private String duration;
    private String fullName;
    private String shortName;
    private String type;

    public courseList() {
    }



    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getDepCode() {
        return depCode;
    }

    public void setDepCode(String depCode) {
        this.depCode = depCode;
    }

    public String getDepName() {
        return depName;
    }

    public void setDepName(String depName) {
        this.depName = depName;
    }

    public String getDepShortName() {
        return depShortName;
    }

    public void setDepShortName(String depShortName) {
        this.depShortName = depShortName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
