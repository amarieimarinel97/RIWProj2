package com.tuiasi.dns;

import com.tuiasi.dns.utils.RCodeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

import static com.tuiasi.dns.utils.DNSUtils.readIPAddress;

@Data
@Builder
@AllArgsConstructor
public class DNSResponse {
    private List<byte[]> records;
    private String IPAddress;


    private long transactionID;
    private long flags;
    private RCodeType RCode;
    private long questions;
    private long answers;
    private long authority;
    private long additional;
    private long recordType;
    private long class1;
    private long field;
    private long type;
    private long class2;
    private long timeToLive;

    public DNSResponse(byte[] response) throws IOException {

        DataInputStream din = new DataInputStream(new ByteArrayInputStream(response));
        this.transactionID = din.readShort();
        this.flags = din.readShort();
        this.RCode = RCodeType.values()[(int) (this.flags & 0xF)]; //RCode == 0
        if(this.RCode.ordinal() != 0)
            throw new InvalidObjectException("Error: RCode = " + this.RCode.name());
        this.questions = din.readShort();
        this.answers = din.readShort();
        if(this.answers<=0)
            throw new InvalidObjectException("Error: Answers Count = "+this.answers);

        this.authority = din.readShort();
        this.additional = din.readShort();

        this.records = new ArrayList<>();
        int recLen = 0;
        while ((recLen = din.readByte()) > 0) {
            byte[] record = new byte[recLen];

            for (int i = 0; i < recLen; i++)
                record[i] = din.readByte();
            this.records.add(record);
        }

        this.recordType = din.readShort(); // RecordType == 1 -> IPv4
        this.class1 = din.readShort();
        this.field = din.readShort();
        this.type = din.readShort();
        this.class2 = din.readShort();
        this.timeToLive = din.readInt();

        this.IPAddress = readIPAddress(din);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Transaction ID: 0x").append(String.format("%x", transactionID)).append("\n");
        sb.append("Flags: 0x").append(String.format("%X", flags & 0xFFFF)).append("\n");
        sb.append("RCode: 0x").append(String.format("%X", RCode.ordinal())).append("\n");
        sb.append("Questions: 0x").append(String.format("%x", questions)).append("\n");
        sb.append("Answers RRs: 0x").append(String.format("%x", answers)).append("\n");
        sb.append("Authority RRs: 0x").append(String.format("%x", authority)).append("\n");
        sb.append("Additional RRs: 0x").append(String.format("%x", additional)).append("\n");
        for (byte[] record : records)
            sb.append("Record: ").append(new String(record)).append("\n");
        sb.append("Record Type: 0x").append(String.format("%x", recordType)).append("\n");

        sb.append("Class: 0x").append(String.format("%x", class1)).append("\n");
        sb.append("Field: 0x").append(String.format("%x", field & 0xFFFF)).append("\n");
        sb.append("Type: 0x").append(String.format("%x", type)).append("\n");
        sb.append("Class: 0x").append(String.format("%x", class2)).append("\n");
        sb.append("TTL: 0x").append(String.format("%x", timeToLive)).append("\n");
        sb.append("IP Address: ").append(IPAddress).append("\n");


        return sb.toString();
    }

}
