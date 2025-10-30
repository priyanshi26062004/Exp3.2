package com.hibernate.crud;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import jakarta.persistence.*;

// Student entity
@Entity
@Table(name = "student")
class Student {
    @Id
    private int id;
    private String name;
    private String department;
    private double marks;

    public Student() {}

    public Student(int id, String name, String department, double marks) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.marks = marks;
    }

    public String toString() {
        return id + " | " + name + " | " + department + " | " + marks;
    }
}

// Main application
public class HibernateStudentCRUD {
    public static void main(String[] args) {
        // Step 1: Configure Hibernate
        Configuration cfg = new Configuration()
                .configure("hibernate.cfg.xml")
                .addAnnotatedClass(Student.class);

        SessionFactory factory = cfg.buildSessionFactory();
        Session session = factory.openSession();

        // Step 2: CREATE
        Transaction tx = session.beginTransaction();
        session.save(new Student(1, "Priya", "CSE", 85.5));
        session.save(new Student(2, "Amit", "IT", 90.2));
        tx.commit();
        System.out.println("✅ Students inserted successfully!");

        // Step 3: READ
        Student s = session.get(Student.class, 1);
        System.out.println("🔹 Read Student: " + s);

        // Step 4: UPDATE
        tx = session.beginTransaction();
        Student s2 = session.get(Student.class, 2);
        s2.marks = 95.0;
        session.update(s2);
        tx.commit();
        System.out.println("🔸 Updated: " + s2);

        // Step 5: DELETE
        tx = session.beginTransaction();
        Student del = session.get(Student.class, 1);
        session.delete(del);
        tx.commit();
        System.out.println("❌ Deleted: " + del);

        session.close();
        factory.close();
    }
}
