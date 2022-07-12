package com.nelsonaraujo.academicorganizer.Models;

import java.io.Serializable;
import java.util.Date;

public class Instructor implements Serializable {
    public static final long serialVersionUID = 20220712L; // Use integer date for base serialization

    private long m_Id;
    private final String mName; // Final to prevent it from being changed without the db being updated.
    private final String mPhone;
    private final String mEmail;

    /**
     * Class constructor.
     * @param id Instructor id.
     * @param name Instructor name.
     * @param phone Instructor phone number.
     * @param email Instructor email address.
     */
    public Instructor(long id, String name, String phone, String email) {
        this.m_Id = id;
        this.mName = name;
        this.mPhone = phone;
        this.mEmail = email;
    }

    // Getters
    public long getId() {
        return m_Id;
    }

    public String getName() {
        return mName;
    }

    public String getPhone() {
        return mPhone;
    }

    public String getEmail() {
        return mEmail;
    }

    // Setters
    public void setId(long id) {
        this.m_Id = id;
    }

    /**
     * Print instructor values in plain text.
     * @return Course values.
     */
    @Override
    public String toString() {
        return "Instructor{" +
                "Id=" + m_Id +
                ", Name='" + mName + '\'' +
                ", Phone='" + mPhone + '\'' +
                ", Email='" + mEmail + '\'' +
                '}';
    }
}
