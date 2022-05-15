package com.work.wushig.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * (VCommonUsedTopical)实体类
 *
 * @author gaojianjun
 * @since 2022-04-14 09:38:01
 */
public class CommonUsedTopical extends ESEntity implements Serializable {

    private String stdUuid;

    private Integer stdId;

    private String stdNo;

    private String stdNoBh;

    private String stdNoHm;

    private String stdNoSplit;

    private String stdNoYear;

    private String stdName;

    private Integer stdType;

    private Integer stdState;

    private Object stdCategory;

    private Date stdPublishDate;

    private Date stdDoDate;

    private Date stdExpiredDate;

    private Integer hasPdfFile;

    private String stdPdfFile;

    private Integer hasStruct;

    private Integer isOpenStd;

    private String inputBy;

    private Date inputAt;

    private String processBy;

    private Date processAt;

    private String approveBy;

    private Date approveAt;

    private Integer approveStatus;

    private List<Date> inputAtTimes;

    private List<Date> approvalAtTimes;

    private List<Date> processAtTimes;

    private List<Date> stdPublishDates;

    private List<Date> stdDoDates;

    private List<Date> stdExpiredDates;

    private Integer pageNo;

    private Integer pageSize;

    private Integer pageNum;

    private String searchValue;

    private String searchValueForNameOrNo;

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getSearchValueForNameOrNo() {
        return searchValueForNameOrNo;
    }

    public void setSearchValueForNameOrNo(String searchValueForNameOrNo) {
        this.searchValueForNameOrNo = searchValueForNameOrNo;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public String getStdUuid() {
        return stdUuid;
    }

    public void setStdUuid(String stdUuid) {
        this.stdUuid = stdUuid;
    }

    public Integer getStdId() {
        return stdId;
    }

    public void setStdId(Integer stdId) {
        this.stdId = stdId;
    }

    public String getStdNo() {
        return stdNo;
    }

    public void setStdNo(String stdNo) {
        this.stdNo = stdNo;
    }

    public String getStdNoBh() {
        return stdNoBh;
    }

    public void setStdNoBh(String stdNoBh) {
        this.stdNoBh = stdNoBh;
    }

    public String getStdNoHm() {
        return stdNoHm;
    }

    public void setStdNoHm(String stdNoHm) {
        this.stdNoHm = stdNoHm;
    }

    public String getStdNoSplit() {
        return stdNoSplit;
    }

    public void setStdNoSplit(String stdNoSplit) {
        this.stdNoSplit = stdNoSplit;
    }

    public String getStdNoYear() {
        return stdNoYear;
    }

    public void setStdNoYear(String stdNoYear) {
        this.stdNoYear = stdNoYear;
    }

    public String getStdName() {
        return stdName;
    }

    public void setStdName(String stdName) {
        this.stdName = stdName;
    }

    public Integer getStdType() {
        return stdType;
    }

    public void setStdType(Integer stdType) {
        this.stdType = stdType;
    }

    public Integer getStdState() {
        return stdState;
    }

    public void setStdState(Integer stdState) {
        this.stdState = stdState;
    }

    public Object getStdCategory() {
        return stdCategory;
    }

    public void setStdCategory(Object stdCategory) {
        this.stdCategory = stdCategory;
    }

    public Date getStdPublishDate() {
        return stdPublishDate;
    }

    public void setStdPublishDate(Date stdPublishDate) {
        this.stdPublishDate = stdPublishDate;
    }

    public Date getStdDoDate() {
        return stdDoDate;
    }

    public void setStdDoDate(Date stdDoDate) {
        this.stdDoDate = stdDoDate;
    }

    public Date getStdExpiredDate() {
        return stdExpiredDate;
    }

    public void setStdExpiredDate(Date stdExpiredDate) {
        this.stdExpiredDate = stdExpiredDate;
    }

    public Integer getHasPdfFile() {
        return hasPdfFile;
    }

    public void setHasPdfFile(Integer hasPdfFile) {
        this.hasPdfFile = hasPdfFile;
    }

    public String getStdPdfFile() {
        return stdPdfFile;
    }

    public void setStdPdfFile(String stdPdfFile) {
        this.stdPdfFile = stdPdfFile;
    }

    public Integer getHasStruct() {
        return hasStruct;
    }

    public void setHasStruct(Integer hasStruct) {
        this.hasStruct = hasStruct;
    }

    public Integer getIsOpenStd() {
        return isOpenStd;
    }

    public void setIsOpenStd(Integer isOpenStd) {
        this.isOpenStd = isOpenStd;
    }

    public String getInputBy() {
        return inputBy;
    }

    public void setInputBy(String inputBy) {
        this.inputBy = inputBy;
    }

    public Date getInputAt() {
        return inputAt;
    }

    public void setInputAt(Date inputAt) {
        this.inputAt = inputAt;
    }

    public String getProcessBy() {
        return processBy;
    }

    public void setProcessBy(String processBy) {
        this.processBy = processBy;
    }

    public Date getProcessAt() {
        return processAt;
    }

    public void setProcessAt(Date processAt) {
        this.processAt = processAt;
    }

    public String getApproveBy() {
        return approveBy;
    }

    public void setApproveBy(String approveBy) {
        this.approveBy = approveBy;
    }

    public Date getApproveAt() {
        return approveAt;
    }

    public void setApproveAt(Date approveAt) {
        this.approveAt = approveAt;
    }

    public Integer getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(Integer approveStatus) {
        this.approveStatus = approveStatus;
    }

    public List<Date> getInputAtTimes() {
        return inputAtTimes;
    }

    public void setInputAtTimes(List<Date> inputAtTimes) {
        this.inputAtTimes = inputAtTimes;
    }

    public List<Date> getApprovalAtTimes() {
        return approvalAtTimes;
    }

    public void setApprovalAtTimes(List<Date> approvalAtTimes) {
        this.approvalAtTimes = approvalAtTimes;
    }

    public List<Date> getProcessAtTimes() {
        return processAtTimes;
    }

    public void setProcessAtTimes(List<Date> processAtTimes) {
        this.processAtTimes = processAtTimes;
    }

    public List<Date> getStdPublishDates() {
        return stdPublishDates;
    }

    public void setStdPublishDates(List<Date> stdPublishDates) {
        this.stdPublishDates = stdPublishDates;
    }

    public List<Date> getStdDoDates() {
        return stdDoDates;
    }

    public void setStdDoDates(List<Date> stdDoDates) {
        this.stdDoDates = stdDoDates;
    }

    public List<Date> getStdExpiredDates() {
        return stdExpiredDates;
    }

    public void setStdExpiredDates(List<Date> stdExpiredDates) {
        this.stdExpiredDates = stdExpiredDates;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
