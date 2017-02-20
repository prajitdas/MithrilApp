package edu.umbc.cs.ebiquity.mithril.util.specialtasks.detect.policyconflicts;

import edu.umbc.cs.ebiquity.mithril.data.model.rules.actions.Action;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.context.SemanticUserContext;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.protectedresources.Resource;
import edu.umbc.cs.ebiquity.mithril.data.model.rules.requesters.Requester;

/**
 * Created by prajit on 12/13/16.
 */

public class PolicyConflictDetector {
    private SemanticUserContext semanticUserContext;
    private Requester requester;
    private Resource resource;
    private Action action;

    /**
     * Algorithm for violation detection:
     * Design assumption: Given a policy, a policy rule, has
     *      a) a requester = app being launched
     *      b) a resource = something that the app starts (we are not doing this, maybe we should, if we can) how about it's the permission that the app possesses?
     *      c) a context = includes location, activity, presence info, temporal info with respect to a user's state
     *      d) an action = We are using Closed World Assumption (CWA) in this system. We will only state what is allowed.
     *      The assumption is that if a particular scenario is not stated explicitly then it's a case when action is to deny.
     *      -> These attributes are then used by our system to handle access control decisions or to determine policy violations depending on what state the system is in.
     * 1) When a request from an app is detected, select all policy rules that contain the current requester-resource combo.
     * 2) Determine the context information
     * 3) Search policy list for a rule that matches the current combo of requester-resource-context combo
     * 4) If no such rule is found then detect this as a violation and request a user action on it or if in execution mode, block this access
     */
}
