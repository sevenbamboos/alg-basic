package com.samwang.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.samwang.util.Utility.*;

public enum Utility {
	;
    public static Optional<String> notEmpty(String s) {

	    return s != null && !s.isEmpty() ? Optional.of(s) : Optional.empty();
    }

    public static Optional<String> notBlank(String s) {
        return s != null && !s.trim().isEmpty() ? Optional.of(s) : Optional.empty();
    }

    public static void require(boolean b, Optional<String> msg) {
	    if (!b) {
	        throw new IllegalStateException(msg.orElse("Pre-condition failed due to unknown reason"));
        }
    }

    public static <R,T1> Optional<R> get(T1 root, Function<T1,R> prop1) {
        return root == null
            ? Optional.empty()
            : Optional.ofNullable(prop1.apply(root));
    }

    public static <R,T1,T2> Optional<R> get(T1 root, Function<T1,T2> prop1, Function<T2,R> prop2) {
        return get(root, prop1).map(t -> prop2.apply(t));
    }

    public static <R,T1,T2,T3> Optional<R> get(T1 root, Function<T1,T2> prop1, Function<T2,T3> prop2, Function<T3,R> prop3) {
        return get(root, prop1, prop2).map(t -> prop3.apply(t));
    }

    public static <R,T1,T2,T3,T4> Optional<R> get(T1 root, Function<T1,T2> prop1, Function<T2,T3> prop2, Function<T3,T4> prop3, Function<T4,R> prop4) {
        return get(root, prop1, prop2, prop3).map(t -> prop4.apply(t));
    }

    static class Student {
        Teacher teacher;

        public Teacher getTeacher() {
            return teacher;
        }

        public void setTeacher(Teacher teacher) {
            this.teacher = teacher;
        }
    }

    static class Teacher {
        Email email;

        public Email getEmail() {
            return email;
        }

        public void setEmail(Email email) {
            this.email = email;
        }
    }

    static class Email {
        String address;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    private static String teacherEmailWithEarlyBreak(Student student) {

        if (student == null) return null;
        if (student.getTeacher() == null) return null;
        if (student.getTeacher().getEmail() == null) return null;

        return student.getTeacher().getEmail().getAddress();
    }

    private static String teacherEmailWithIf(Student student) {

        if (student != null
            && student.getTeacher() != null
            && student.getTeacher().getEmail() != null) {

            return student.getTeacher().getEmail().getAddress();

        } else {
            return null;
        }
    }

    private static String teacherEmailWithPropertyAccessor(Student student) {

        return get(student,
            Student::getTeacher,
            Teacher::getEmail,
            Email::getAddress

        ).orElse(null);
    }

    private static Optional<String> teacherEmail(Student student) {
        return get(student,
            Student::getTeacher,
            Teacher::getEmail,
            Email::getAddress);
    }
    
    private static void changeEmail(Student student, Function<Student, String> emailRetriever) {
        String oldValue = emailRetriever.apply(student);
        if (oldValue != null) {
            System.out.println("Old value:" + oldValue);
            System.out.println("New value:" + oldValue.replaceFirst("qq", "163"));
            System.out.println();
        }
    }

    public static void main(String[] args) {

        String oldMail = "icanfly@qq.com";

        Email aEmail = new Email();
        aEmail.setAddress(oldMail);

        Teacher aTeacher = new Teacher();
        aTeacher.setEmail(aEmail);

        Student aStudent = new Student();
        aStudent.setTeacher(aTeacher);

        System.out.println("teacherEmailWithEarlyBreak");
        changeEmail(aStudent, Utility::teacherEmailWithEarlyBreak);

        System.out.println("teacherEmailWithIf");
        changeEmail(aStudent, Utility::teacherEmailWithIf);

        System.out.println("teacherEmailWithPropertyAccessor");
        changeEmail(aStudent, Utility::teacherEmailWithPropertyAccessor);

        System.out.println("teacherEmail");
        teacherEmail(aStudent)
            .map(s -> s.replaceFirst("qq", "163"))
            .map(s -> "New value:" + s)
            .ifPresent(System.out::println);
    }

}
