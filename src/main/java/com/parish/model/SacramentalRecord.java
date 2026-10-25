package com.parish.model;

public class SacramentalRecord {
    private int recordId;
    private int parishionerId;
    private String sacramentType;
    private String sacramentDate;
    private String officiatingPriest;

    public SacramentalRecord(int recordId, int parishionerId, String sacramentType, String sacramentDate, String officiatingPriest) {
        this.recordId = recordId;
        this.parishionerId = parishionerId;
        this.sacramentType = sacramentType;
        this.sacramentDate = sacramentDate;
        this.officiatingPriest = officiatingPriest;
    }

    public int getRecordId() { return recordId; }
    public int getParishionerId() { return parishionerId; }
    public String getSacramentType() { return sacramentType; }
    public String getSacramentDate() { return sacramentDate; }
    public String getOfficiatingPriest() { return officiatingPriest; }
}