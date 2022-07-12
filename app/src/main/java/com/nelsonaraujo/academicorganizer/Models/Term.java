package com.nelsonaraujo.academicorganizer.Models;

import java.io.Serializable;

public class Term implements Serializable {
    public static final long serialVersionUID = 20220605L; // Used date for base serialization

    private long m_Id;
    private final String mTitle; // Final to prevent it from being changed without the db being updated.
    private final String mStart;
    private final String mEnd;

    public Term(long id, String title, String start, String end) {
        this.m_Id = id;
        this.mTitle = title;
        this.mStart = start;
        this.mEnd = end;
    }

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

    public void setId(long id) {
        this.m_Id = id;
    }

    @Override
    public String toString() {
        return "Term{" +
                "m_Id=" + m_Id +
                ", mTitle='" + mTitle + '\'' +
                ", mStart='" + mStart + '\'' +
                ", mEnd='" + mEnd + '\'' +
                '}';
    }
}
