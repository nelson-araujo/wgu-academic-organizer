package com.nelsonaraujo.academicorganizer.Models;

import java.io.Serializable;
import java.sql.Date;

/**
 * Course data structure.
 */
public class Course implements Serializable {
    public static final long serialVersionUID = 20220712L; // Use date integer for base serialization

    private long m_Id;
    private final String mTitle; // Final to prevent it from being changed without the db being updated.
    private final Date mStart;
    private final Date mEnd;
    private final String mStatus;
    private final String mNote;
    private final Integer mTermId;
    private final Integer mInstructorId;

    /**
     * Class constructor.
     * @param id Course id.
     * @param title Course title.
     * @param start Course start date.
     * @param end Course end date.
     * @param status Course status.
     * @param note Course note.
     * @param termId Term id the course falls in.
     * @param instructorId Course instructor id.
     */
    public Course(long id, String title, Date start, Date end, String status, String note, Integer termId, Integer instructorId) {
        this.m_Id = id;
        this.mTitle = title;
        this.mStart = start;
        this.mEnd = end;
        this.mStatus = status;
        this.mNote = note;
        this.mTermId = termId;
        this.mInstructorId = instructorId;
    }

    // Getters
    public long getId() {
        return m_Id;
    }

    public String getTitle() {
        return mTitle;
    }

    public Date getStart() {
        return mStart;
    }

    public Date getEnd() {
        return mEnd;
    }

    public String getStatus() {
        return mStatus;
    }

    public String getNote() {
        return mNote;
    }

    public Integer getTermId() {
        return mTermId;
    }

    public Integer getInstructorId() {
        return mInstructorId;
    }

    // Setters
    public void setId(long id) {
        this.m_Id = id;
    }

    /**
     * Print course values in plain text.
     * @return Course values.
     */
    @Override
    public String toString() {
        return "Course{" +
                "Id=" + m_Id +
                ", Title='" + mTitle + '\'' +
                ", Start=" + mStart +
                ", End=" + mEnd +
                ", Status='" + mStatus + '\'' +
                ", Note='" + mNote + '\'' +
                ", TermId=" + mTermId +
                ", InstructorId=" + mInstructorId +
                '}';
    }
}
