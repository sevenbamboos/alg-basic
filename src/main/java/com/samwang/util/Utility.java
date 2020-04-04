package com.samwang.util;

import com.samwang.common.Tuple;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.samwang.common.Tuple.Tuple;

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

    public static <T> List<Tuple<T,Integer>> zipWithIndex(List<T> lstT) {
        return _zip(
            new ArrayList<>(),
            lstT.iterator(),
            IntStream.range(0, lstT.size())
                .mapToObj(java.lang.Integer::new)
                .collect(Collectors.toList()).iterator());
    }

    public static <T,U> List<Tuple<T,U>> zip(T[] arrT, U[] arrU) {
        return _zip(new ArrayList<>(), arrT, arrU, 0);
    }

    private static <T,U> List<Tuple<T,U>> _zip(List<Tuple<T,U>> acc, T[] arrT, U[] arrU, int index) {
        T t = arrT != null && index < arrT.length ? arrT[index] : null;
        U u = arrU != null && index < arrU.length ? arrU[index] : null;

        if (t == null && u == null) return acc;
        acc.add(Tuple(t, u));
        return _zip(acc, arrT, arrU, index+1);
    }

    public static <T,U> List<Tuple<T,U>> zip(List<T> lstT, List<U> lstU) {
        return _zip(new ArrayList<>(),
            Objects.requireNonNull(lstT).iterator(),
            Objects.requireNonNull(lstU).iterator());
    }

    private static <T,U> List<Tuple<T,U>> _zip(List<Tuple<T,U>> acc, Iterator<T> iteT, Iterator<U> iteU) {
        T t = iteT.hasNext() ? iteT.next() : null;
        U u = iteU.hasNext() ? iteU.next() : null;

        if (t == null && u == null) return acc;
        acc.add(Tuple(t, u));
        return _zip(acc, iteT, iteU);
    }

    public static <T> T[] tail(T[] array) {
        if (Objects.isNull(array) || array.length < 2) return null;
        return Arrays.copyOfRange(array, 1, array.length);
    }

    public static <T> Optional<T> toOption(T value, Predicate<T> isEmpty) {
        return
            Objects.isNull(value)
                ? Optional.empty()
                : Objects.isNull(isEmpty)
                    ? Optional.of(value)
                    : isEmpty.test(value)
                        ? Optional.empty()
                        : Optional.of(value);
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

    public static String fmtNameBeforeJava8(String emptyPlaceHolder, String delim, String... names) {
        return join(names, emptyPlaceHolder, delim, 0, new StringBuilder());
    }

    private static String join(String[] arr, String emptyPlaceHolder, String delim, int index, StringBuilder accumulator) {
        if (index >= arr.length) return accumulator.toString();
        if (index > 0) accumulator.append(delim);
        return join(arr, emptyPlaceHolder, delim, index+1, accumulator.append(notEmptyOrDefault(arr[index], emptyPlaceHolder)));
    }

    private static String notEmptyOrDefault(String str, String defaultValue) {
        return str == null || str.trim().isEmpty() ? defaultValue : str;
    }

    public static void main(String[] args) {
        System.out.println("|>" + fmtNameBeforeJava8("[Not Provided]", "|"));
    }

    private static void oldMain() {
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
