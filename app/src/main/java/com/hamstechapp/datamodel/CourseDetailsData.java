package com.hamstechapp.datamodel;

public class CourseDetailsData {

    String courseId;
    String sem_id;
    String sem_name;
    String categoryId;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getSem_id() {
        return sem_id;
    }

    public void setSem_id(String sem_id) {
        this.sem_id = sem_id;
    }

    public String getSem_name() {
        return sem_name;
    }

    public void setSem_name(String sem_name) {
        this.sem_name = sem_name;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(String curriculum) {
        this.curriculum = curriculum;
    }

    String curriculum;
}
