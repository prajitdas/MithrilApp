package edu.umbc.ebiquity.mithril.simulations;

import java.util.ArrayList;
import java.util.List;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.data.dbhelpers.MithrilDBHelper;
import edu.umbc.ebiquity.mithril.data.model.PolicyRule;
import edu.umbc.ebiquity.mithril.data.model.rules.actions.Action;
import edu.umbc.ebiquity.mithril.data.model.rules.actions.RuleAction;
import edu.umbc.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticActivity;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticLocation;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticNearActor;
import edu.umbc.ebiquity.mithril.data.model.rules.context.contextpieces.SemanticTime;

public class DataGenerator {
    /**
     * This data generator is used in the loadDefaultDataIntoDB method in {@link MithrilDBHelper} to generate default policies
     *
     * @param id
     * @param name
     * @param ctxId
     * @param appId
     * @param action
     * @param semanticUserContext
     * @return
     */
    public static PolicyRule generateSocialMediaCameraAccessRule(int id, String name, int ctxId, int appId, RuleAction action, SemanticUserContext semanticUserContext) {
        PolicyRule policyRule = new PolicyRule(id, name, ctxId, appId, action, semanticUserContext);
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

        SemanticUserContext userContext = new SemanticUserContext(
                new SemanticNearActor(MithrilApplication.getContextDefaultRelationship()), // Boss
                new SemanticActivity(tempActivityString.get(2)), //Index 2 is university talk
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
}