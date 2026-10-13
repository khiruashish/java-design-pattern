package com.handbook.lld.library;

import com.handbook.lld.library.Library.Member;
import com.handbook.lld.library.Library.MemberType;

import java.time.LocalDate;

public class LibraryDemo {

    public static void main(String[] args) {
        var library = new Library(Library.standardFines(),
                (member, message) -> System.out.println("    notify " + member.name() + ": " + message));
        library.addBook("978-0134685991", "Effective Java", 1);
        library.addBook("978-0201633610", "Design Patterns (GoF)", 2);
        Member asha = new Member("M1", "Asha", MemberType.STUDENT);
        Member ravi = new Member("M2", "Ravi", MemberType.STUDENT);
        Member drIyer = new Member("M3", "Dr. Iyer", MemberType.FACULTY);
        LocalDate day1 = LocalDate.of(2026, 10, 1);

        System.out.println("=== LLD: Library management ===");
        System.out.println("Oct 1 : " + library.borrow(asha, "978-0134685991", day1));
        System.out.println("Oct 1 : " + library.borrow(ravi, "978-0134685991", day1));
        System.out.println("Oct 1 : " + library.borrow(drIyer, "978-0134685991", day1));
        System.out.println("Oct 1 : " + library.borrow(asha, "978-0201633610", day1));
        System.out.println("Oct 1 : " + library.borrow(asha, "978-0201633610", day1));
        System.out.println("Oct 21: " + library.giveBack(asha, "978-0134685991", day1.plusDays(20)));
        System.out.println("Oct 21: " + library.borrow(drIyer, "978-0134685991", day1.plusDays(20)));
        System.out.println("Oct 22: " + library.borrow(ravi, "978-0134685991", day1.plusDays(21)));
        System.out.println("Dec 21: " + library.giveBack(ravi, "978-0134685991", day1.plusDays(81)));
        System.out.println("Dec 21: " + library.borrow(drIyer, "978-0134685991", day1.plusDays(81)));
    }
}
