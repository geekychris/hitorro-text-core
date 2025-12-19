/*
 * Copyright (c) 2006-2025 Chris Collins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.hitorro.obj.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple naive parser for course semester string decomposition.
 * <p>
 * General approach:
 * uses simple surface form tokenizer to split a provided string along surface for changes
 * These tokens then parse left to right the parts from the tokens.
 */
public class CourseDecoder {
    public static CourseSelection parse(String text) {
        CourseSelection cs = new CourseSelection();
        cs.originalText = text;
        List<String> parts = getParts(text);
        if (parts.size() < 3) {
            return cs;
        }

        cs.department = parts.get(0);

        if (parts.size() == 3) {
            handle3Parts(cs, parts);
        } else {
            handle4Parts(cs, parts);
        }

        return cs;
    }

    private static void handle4Parts(final CourseSelection cs, final List<String> parts) {
        cs.courseNumber = parts.get(1);
        SurfaceType type = getType(parts.get(2));
        if (type == SurfaceType.Number) {
            cs.year = parts.get(2);
            cs.semester = getSemester(parts.get(3));
        } else {
            cs.year = parts.get(3);
            cs.semester = getSemester(parts.get(2));
        }
    }

    /**
     * Not called for in the spec but handle anyway where course number and year have no seperation
     *
     * @param cs
     * @param parts
     */
    private static void handle3Parts(final CourseSelection cs,
                                     final List<String> parts) {
        //
        SurfaceType type = getType(parts.get(1));
        if (type == SurfaceType.Number) {
            setCourYearSemester3Parts(cs, parts, 1, 2);
        } else {
            setCourYearSemester3Parts(cs, parts, 2, 1);
        }
    }

    private static void setCourYearSemester3Parts(final CourseSelection cs,
                                                  final List<String> parts,
                                                  final int i,
                                                  final int i2) {
        String t = parts.get(i);
        setCourseYearFor3Parts(cs, t);
        cs.semester = getSemester(parts.get(i2));
    }

    private static void setCourseYearFor3Parts(final CourseSelection cs,
                                               final String t) {
        cs.year = t.substring(t.length() - 4, t.length());
        cs.courseNumber = t.substring(0, t.length() - 4);
    }

    private static String getSemester(String txt) {
        if (txt == null || txt.length() == 0) {
            return null;
        }
        char c = Character.toLowerCase(txt.charAt(0));
        switch (c) {
            case 'f':
                return "Fall";
            case 'w':
                return "Winter";
            case 's':
                char sec = Character.toLowerCase(txt.charAt(1));
                switch (sec) {
                    case 'u':
                        return "Summer";
                    default:
                        return "Spring";
                }
        }
        return null;
    }

    private static List<String> getParts(String text) {
        List<String> res = new ArrayList();
        int e = text.length() - 1;
        int s = 0;
        while (s <= e) {
            int i = CourseDecoder.scanWhileLikeChar(text, s, e);
            if (i == -1) {
                break;
            }
            String part = text.substring(s, i + 1);
            SurfaceType st = getType(part);
            {
                if (st == SurfaceType.Char || st == SurfaceType.Number) {
                    res.add(part);
                }
            }

            s = i + 1;
        }
        return res;
    }

    public static int scanWhileLikeChar(String text,
                                        int s,
                                        int e) {
        if (s > e) {
            return -1;
        }
        if (s == e) {
            return s;
        }
        char c = text.charAt(s);
        SurfaceType type = getType(c);
        for (int i = s + 1; i <= e; i++) {
            char curr = text.charAt(i);
            SurfaceType currType = getType(curr);
            if (type != currType) {
                return i - 1;
            }
        }
        return e;
    }

    /**
     * Get the surface form type.  Assumes that string contains all of the same type
     * so sniffing the first character if available is sufficient.
     *
     * @param s
     * @return
     */
    public static SurfaceType getType(String s) {
        if (s == null || s.length() == 0) {
            return SurfaceType.Other;
        }
        return getType(s.charAt(0));
    }

    public static SurfaceType getType(char c) {
        if (Character.isDigit(c)) {
            return SurfaceType.Number;
        }
        if (Character.isLetter(c)) {
            return SurfaceType.Char;
        }
        return SurfaceType.Other;
    }

    enum SurfaceType {
        Char, Number, Other
    }
}

class CourseSelection {
    String originalText;
    String department;
    String courseNumber;
    String semester;
    String year;
    boolean parsed = false;

    public CourseSelection() {

    }

    public CourseSelection(String originalText, String department, String courseNumber, String year, String semester) {
        this.originalText = originalText;
        this.department = department;
        this.courseNumber = courseNumber;
        this.semester = semester;
        this.year = year;
    }

    public String getOriginalText() {
        return originalText;
    }

    public boolean equals(Object o) {
        if (o instanceof CourseSelection) {
            CourseSelection other = (CourseSelection) o;
            return department.equals(other.department) &&
                    courseNumber.equals(other.courseNumber) &&
                    semester.equals(other.semester) &&
                    year.equals(other.year);
        }
        return false;
    }
}

class CourseTest {
    static CourseSelection examples[] = {
            new CourseSelection("CS111 2016 Fall",
                    "CS",
                    "111",
                    "2016",
                    "Fall"),
            new CourseSelection("CS-111 Fall 2016",
                    "CS",
                    "111",
                    "2016",
                    "Fall"),
            new CourseSelection("CS 111 F2016",
                    "CS",
                    "111",
                    "2016",
                    "Fall"),
            new CourseSelection("C112016F",
                    "CS",
                    "11",
                    "2016",
                    "Fall")};

    public static void test() {
        for (CourseSelection cs : examples) {
            CourseSelection csResult = CourseDecoder.parse(cs.getOriginalText());
            if (csResult == null) {
                System.out.println("Failed to parse: " + cs.getOriginalText());
            } else if (cs.equals(csResult)) {
                System.out.println("Pass: " + cs.getOriginalText());
            } else {
                System.out.println("Fail: " + cs.getOriginalText());
            }
        }
    }

    /**
     * basic function to allow me to debug the surface form scanner
     */
    public static void testSurfaceScan() {
        String txt = "CS 111 F2016";
        int e = txt.length() - 1;
        int s = 0;

        while (s <= e) {
            int i = CourseDecoder.scanWhileLikeChar(txt, s, e);
            if (i == -1) {
                break;
            }
            String part = txt.substring(s, i + 1);
            System.out.println(part);
            s = i + 1;
        }

    }
}
