package com.nelsonaraujo.academicorganizer.Models;

import java.io.Serializable;

/**
 * Assessment data structure.
 */
public class Assessment implements Serializable {
    public static final long serialVersionUID = 20220712L; // Use integer date for base serialization

    private long m_Id;
    private final String mTitle; // Final to prevent it from being changed without the db being updated.
    private final String mStart;
    private final String mEnd;
    private final String mContent;
    private final Integer mCourseId;

    /**
     * Class constructor.
     * @param id Assessment id.
     * @param title Assessment title.
     * @param start Assessment start date.
     * @param end Assessment end date.
     * @param content Assessment content.
     * @param courseId Course id the assessment belongs to.
     */
    public Assessment(long id, String title, String start, String end, String content, Integer courseId) {
        this.m_Id = id;
        this.mTitle = title;
        this.mStart = start;
        this.mEnd = end;
        this.mContent = content;
        this.mCourseId =  courseId;
    }

    // Getters
    public long getId() {
        return m_Id;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getStart() {
        return mStart;
    }

    public String getEnd() {
        return mEnd;
    }

    public String getContent() {
        return mContent;
    }

    public Integer getCourseId() {
        return mCourseId;
    }

    // Setters
    public void setId(long id) {
        this.m_Id = id;
    }

    /**
     * Print assessment values in plain text.
     * @return Assessment values.
     */
    @Override
    public String toString() {
        return "Assessment{" +
                "Id=" + m_Id +
                ", Title='" + mTitle + '\'' +
                ", Start=" + mStart +
                ", End=" + mEnd +
                ", Content='" + mContent + '\'' +
                ", CourseId=" + mCourseId +
                '}';
    }
}
