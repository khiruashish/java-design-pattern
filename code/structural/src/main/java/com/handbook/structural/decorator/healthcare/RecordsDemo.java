package com.handbook.structural.decorator.healthcare;

import java.util.List;

public class RecordsDemo {

    public static void main(String[] args) {
        System.out.println("=== Healthcare: Patient Record Storage Pipeline ===");
        InMemoryRecordStore disk = new InMemoryRecordStore();
        List<String> audit = AuditingStore.newLog();

        // audit -> compress -> encrypt -> disk  (compress BEFORE encrypt: encrypted data does not compress)
        RecordStore store = new AuditingStore(
                new CompressingStore(new EncryptingStore(disk, "k3y")), "dr.mehta", audit);

        String ecg = "ECG:" + "N".repeat(40) + "V" + "N".repeat(40) + ";BP:high"; // long runs, like real traces
        store.write("P-1001/ecg", ecg);

        System.out.println("Original      (" + ecg.length() + " chars): " + ecg);
        System.out.println("Stored on disk(" + disk.raw("P-1001/ecg").length() + " chars): " + disk.raw("P-1001/ecg"));
        System.out.println("Read back     : " + store.read("P-1001/ecg"));
        System.out.println("Round trip OK : " + ecg.equals(store.read("P-1001/ecg")));
        System.out.println("Audit log     : " + audit);
    }
}
