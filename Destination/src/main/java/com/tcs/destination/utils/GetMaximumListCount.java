package com.tcs.destination.utils;

import java.util.List;
import com.tcs.destination.bean.BidDetailsT;
import com.tcs.destination.bean.ConnectT;
import com.tcs.destination.bean.OpportunityT;
import com.tcs.destination.bean.TaskT;

public class GetMaximumListCount {
	
	public static int getMaxListCount(ConnectT connect, List<String> secondaryOwnersList, String name,int listCount) {
//		int listCount = 1;
//		for (ConnectT connect : connectList) {
			switch (name) {
			case ReportConstants.CUSTOMERCONTACTNAME:
				if (connect.getConnectCustomerContactLinkTs().size() > listCount) {
					listCount = connect.getConnectCustomerContactLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.TCSACCOUNTCONTACT:
				if (connect.getConnectTcsAccountContactLinkTs().size() > listCount) {
					listCount = connect.getConnectTcsAccountContactLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.SECONDARYOWNER:
				if (secondaryOwnersList.size() > listCount) {
					listCount = secondaryOwnersList.size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.TASKNOTES:
				for (TaskT task : connect.getTaskTs()) {
					if (task.getNotesTs().size() > listCount) {
						listCount = task.getNotesTs().size();
					} else {
						listCount = listCount + 0;
					}
				}
				break;
			case ReportConstants.CONNECTNOTES:
//				for (ConnectT connectNotes : connectList) {
					if (connect.getNotesTs().size() > listCount) {
						listCount = connect.getNotesTs().size();
					} else {
						listCount = listCount + 0;
					}
//				}
			case ReportConstants.LINKOPPORTUNITY:
					if(connect.getConnectOpportunityLinkIdTs().size()>listCount){
						listCount=connect.getConnectOpportunityLinkIdTs().size();
					}else{
						listCount=listCount+0;
				}
			default:
				listCount = 1;
				break;
			}
//		}
		return listCount;
	}
	
	public static int getMaxListCount(List<ConnectT> connectList, String name) {
		int listCount = 1;
		for (ConnectT connect : connectList) {
			switch (name) {
			case ReportConstants.CUSTOMERCONTACTNAME:
				if (connect.getConnectCustomerContactLinkTs().size() > listCount) {
					listCount = connect.getConnectCustomerContactLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.TCSACCOUNTCONTACT:
				if (connect.getConnectTcsAccountContactLinkTs().size() > listCount) {
					listCount = connect.getConnectTcsAccountContactLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.TASKNOTES:
				for (TaskT task : connect.getTaskTs()) {
					if (task.getNotesTs().size() > listCount) {
						listCount = task.getNotesTs().size();
					} else {
						listCount = listCount + 0;
					}
				}
				break;
			case ReportConstants.CONNECTNOTES:
				for (ConnectT connectNotes : connectList) {
					if (connectNotes.getNotesTs().size() > listCount) {
						listCount = connectNotes.getNotesTs().size();
					} else {
						listCount = listCount + 0;
					}
				}
			case ReportConstants.LINKOPPORTUNITY:
					if(connect.getConnectOpportunityLinkIdTs().size()>listCount){
						listCount=connect.getConnectOpportunityLinkIdTs().size();
					}else{
						listCount=listCount+0;
				}
			default:
				listCount = 1;
				break;
			}
		}
		return listCount;
	}
	
	public static Integer getMaxOpportunityListCount(List<OpportunityT> opportunityList, String name) {
		int listCount = 1;
		for (OpportunityT opportunity : opportunityList) {
			switch (name) {
			case ReportConstants.CUSTOMERCONTACTNAME:
				if (opportunity.getOpportunityCustomerContactLinkTs().size() > listCount) {
					listCount = opportunity.getOpportunityCustomerContactLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.TCSACCOUNTCONTACT:
				if (opportunity.getOpportunityTcsAccountContactLinkTs().size() > listCount) {
					listCount = opportunity.getOpportunityTcsAccountContactLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.CONNECTNOTES:
				if (opportunity.getNotesTs().size() > listCount) {
					listCount = opportunity.getNotesTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.COMPETITORS:
				if (opportunity.getOpportunityCompetitorLinkTs().size() > listCount) {
					listCount =  opportunity.getOpportunityCompetitorLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.PARTNERSHIPSINVOLVED:
				if (opportunity.getOpportunityPartnerLinkTs().size() > listCount) {
					listCount =  opportunity.getOpportunityPartnerLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.SALESSUPPORTOWNER:
				if (opportunity.getOpportunitySalesSupportLinkTs().size() > listCount) {
					listCount =  opportunity.getOpportunitySalesSupportLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.DEALREMARKSNOTES:
				if (opportunity.getNotesTs().size() > listCount) {
					listCount =  opportunity.getNotesTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.FACTORSFORWINLOSS:
				if (opportunity.getOpportunityWinLossFactorsTs().size() > listCount) {
					listCount =  opportunity.getOpportunityWinLossFactorsTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.OPPORTUNITYLINKID:
				if (opportunity.getConnectOpportunityLinkIdTs().size() > listCount) {
					listCount =  opportunity.getConnectOpportunityLinkIdTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.BIDID:
				if (opportunity.getBidDetailsTs().size() > listCount) {
					listCount =  opportunity.getBidDetailsTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.BIDREQUESTTYPE:
				if (opportunity.getBidDetailsTs().size() > listCount) {
					listCount =  opportunity.getBidDetailsTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.BIDREQUESTRECEIVEDDATE:
				if (opportunity.getBidDetailsTs().size() > listCount) {
					listCount =  opportunity.getBidDetailsTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.BIDOFFICEGROUPOWNER:
				if (opportunity.getBidDetailsTs().size() > listCount) {
					listCount =  opportunity.getBidDetailsTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.TARGETBIDSUBMISSIONDATE:
				if (opportunity.getBidDetailsTs().size() > listCount) {
					listCount =  opportunity.getBidDetailsTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.ACTUALBIDSUBMISSIONDATE:
				if (opportunity.getBidDetailsTs().size() > listCount) {
					listCount =  opportunity.getBidDetailsTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.EXPECTEDDATEOFOUTCOME:
				if (opportunity.getBidDetailsTs().size() > listCount) {
					listCount =  opportunity.getBidDetailsTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.WINPROBABILITY:
				if (opportunity.getBidDetailsTs().size() > listCount) {
					listCount =  opportunity.getBidDetailsTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.COREATTRIBUTESUSEDFORWINNING:
				if (opportunity.getBidDetailsTs().size() > listCount) {
					listCount =  opportunity.getBidDetailsTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			default:
				listCount = 1;
				break;
			}
		}
		return listCount;
	}

	public static Integer getMaxBidDetailsListCount(List<BidDetailsT> bidDetailsList,String field) {
		int listCount = 1;
		for (BidDetailsT bidDetail : bidDetailsList) {
			switch (field) {
			case ReportConstants.TCSACCOUNTCONTACT:
				if (bidDetail.getOpportunityT().getOpportunityTcsAccountContactLinkTs().size() > listCount) {
					listCount = bidDetail.getOpportunityT().getOpportunityTcsAccountContactLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.COMPETITORS:
				if (bidDetail.getOpportunityT().getOpportunityCompetitorLinkTs().size() > listCount) {
					listCount =  bidDetail.getOpportunityT().getOpportunityCompetitorLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			case ReportConstants.BIDOFFICEGROUPOWNER:
				if (bidDetail.getBidOfficeGroupOwnerLinkTs().size() > listCount) {
					listCount =  bidDetail.getBidOfficeGroupOwnerLinkTs().size();
				} else {
					listCount = listCount + 0;
				}
				break;
			default:
				listCount = 1;
				break;
			}
		}
		return listCount;
	}
}
