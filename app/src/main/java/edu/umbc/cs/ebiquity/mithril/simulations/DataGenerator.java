package edu.umbc.cs.ebiquity.mithril.simulations;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.cs.ebiquity.mithril.MithrilApplication;
import edu.umbc.cs.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.PolicyRule;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.Action;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.RuleAction;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticActivity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticIdentity;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticNearActors;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticTime;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.protectedresources.Resource;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.requesters.Requester;

public class DataGenerator {
	/**
	 * This data generator is used in the loadDefaultDataIntoDB method in {@link MithrilDBHelper} to generate default policies
	 * @param aRequester
	 * @param aResource
	 * @param aUserContext
	 * @param aRuleAction
	 * @return
	 */
	public static PolicyRule generateSocialMediaCameraAccessRule(Requester aRequester, Resource aResource, String aUserContext, RuleAction aRuleAction) {
        PolicyRule policyRule = new PolicyRule(MithrilApplication.getPolRulNameSocialMediaCameraAccessRule(), aRequester, aResource, aUserContext, aRuleAction);
        return policyRule;
	}

    public static SemanticUserContext generateContextForSocialMediaCameraAccessRule() {
        List<String> tempIdentityString = new ArrayList<String>();
        for (String tempString : MithrilApplication.getContextArrayPresenceInfoIdentity()) {
            tempIdentityString.add(tempString);
		}
		
		List<String> tempLocationString = new ArrayList<String>();
        for (String tempString : MithrilApplication.getContextArrayLocation()) {
            tempLocationString.add(tempString);
		}

		List<String> tempActivityString = new ArrayList<String>();
        for (String tempString : MithrilApplication.getContextArrayActivity()) {
            tempActivityString.add(tempString);
		}

		List<String> tempTimeString = new ArrayList<String>();
        for (String tempString : MithrilApplication.getContextArrayTime()) {
            tempTimeString.add(tempString);
		}

        List<SemanticIdentity> presenceInfo = new ArrayList<SemanticIdentity>();
        presenceInfo.add(new SemanticIdentity(tempIdentityString.get(2))); // Index 2 is Department Head

        SemanticUserContext userContext = new SemanticUserContext(
                new SemanticNearActors(presenceInfo),
                new SemanticActivity(tempActivityString.get(2)), //Index 2 is university talk
                new SemanticIdentity(MithrilApplication.getContextDefaultIdentity()),
                new SemanticLocation(tempLocationString.get(2)), // Index 2 is university state
                new SemanticTime(tempTimeString.get(2))); // Index 2 is Week Day
        return userContext;
	}
	
	/**
	 * Actions generated
	 */
	public static RuleAction generateAllowAction() {
		return new RuleAction(Action.ALLOW);
	}
	public static RuleAction generateAllowWithCaveatAction() {
		return new RuleAction(Action.ALLOW_WITH_CAVEAT);
	}
	public static RuleAction generateDenyAction() {
		return new RuleAction(Action.DENY);
	}

	/**
	 * Resources generated
	 */ 
	public static List<Resource> generateResources() {
		List<Resource> tempList = new ArrayList<Resource>();
        for (String aResourceString : MithrilApplication.getContextArrayResourceCategory())
            tempList.add(new Resource(aResourceString));
		return tempList;
	}

	/**
	 * Requesters generated
	 */ 
	public static List<Requester> generateRequesters() {
		List<Requester> tempList = new ArrayList<Requester>();
        for (String aRequesterString : MithrilApplication.getContextArrayRequesterCategory())
            tempList.add(new Requester(aRequesterString));
		return tempList;
	}
}