package edu.umbc.ebiquity.mithril.util.specialtasks.semanticweb;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.umbc.ebiquity.mithril.MithrilApplication;
import edu.umbc.ebiquity.mithril.R;
import es.unizar.semantic.DLQueryEngine;
import uk.ac.manchester.cs.jfact.JFactFactory;

/**
 * @author Roberto
 */
@SuppressWarnings("deprecation")
public class SemanticManagement {
    public static final int ontology_id = R.raw.context;

    public OWLOntologyManager manager;
    public OWLDataFactory factory;
    public OWLOntology ontology;
    public OWLReasoner reasoner;
    public DLQueryEngine queryEngine;

    private BidirectionalShortFormProvider bidiShortFormProvider;

    public SemanticManagement(Context context) {
        // Creating OWLManager and OWLDataFactory
        manager = OWLManager.createOWLOntologyManager();
        factory = OWLManager.getOWLDataFactory();

        Log.i(MithrilApplication.getDebugTag(), "Starting...");
        try {
            Log.i(MithrilApplication.getDebugTag(), String.format("Loading ontology [%s] ...", context
                    .getResources().getResourceName(ontology_id)));
            ontology = manager.loadOntologyFromOntologyDocument(context
                    .getResources().openRawResource(ontology_id));

            Log.i(MithrilApplication.getDebugTag(), "Creating reasoner...");
            reasoner = createOWLReasoner(ontology);

            Log.i(MithrilApplication.getDebugTag(), "Creating DLQueryEngine...");
            queryEngine = createDLQueryEngine(reasoner);

            Set<OWLOntology> importsClosure = ontology.getImportsClosure();
            // Create a bidirectional short form provider to do the actual
            // mapping.
            // It will generate names using the input
            // short form provider.
            bidiShortFormProvider = new BidirectionalShortFormProviderAdapter(
                    manager, importsClosure, queryEngine.getShortFormProvider());

            Log.i(MithrilApplication.getDebugTag(), "Precomputing inferences...");
            queryEngine.getReasoner().precomputeInferences(
                    InferenceType.CLASS_HIERARCHY);

        } catch (OWLOntologyCreationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Gets the name of the subclasses of a given class
     *
     * @param SuperClass
     * @return list of names
     */
    public List<String> getNamesSubClasses(String SuperClass) {

        Set<OWLClass> subclasses = queryEngine.getSubClasses(SuperClass, false);

        List<String> names = new ArrayList<String>();
        for (OWLClass aux : subclasses) {
            names.add(aux.getIRI().getFragment());
        }

        return names;
    }

    /**
     * Gets the name of the properties that have the given class in their domain
     *
     * @param Class
     * @return list of names
     */
    public List<String> getNamesProperties(String Class) {

        NodeSet<OWLDataProperty> topDP = reasoner.getSubDataProperties(reasoner
                .getTopDataPropertyNode().getRepresentativeElement(), false);

        List<String> names = new ArrayList<String>();

        for (Node<OWLDataProperty> aux : topDP) {
            for (OWLClassExpression auxCE : aux.getRepresentativeElement()
                    .getDomains(ontology)) {
                // auxCE is the domain of the property
                for (OWLClass auxCl : reasoner.getEquivalentClasses(auxCE)) {
                    // check if the domain is equivalent to the given
                    if (queryEngine.getShortFormProvider().getShortForm(auxCl)
                            .equals(Class)) {
                        // the domain is equivalent to the given
                        names.add(queryEngine.getShortFormProvider()
                                .getShortForm(
                                        aux
                                                .getRepresentativeElement()));
                    }
                }
            }
        }

        NodeSet<OWLObjectPropertyExpression> topOP = reasoner
                .getSubObjectProperties(reasoner.getTopObjectPropertyNode()
                        .getRepresentativeElement(), false);

        for (Node<OWLObjectPropertyExpression> aux : topOP) {
            for (OWLClassExpression auxCE : aux.getRepresentativeElement()
                    .getDomains(ontology)) {
                // auxCE is the domain of the property
                for (OWLClass auxCl : reasoner.getEquivalentClasses(auxCE)) {
                    // check if the domain is equivalent to the given
                    if (queryEngine.getShortFormProvider().getShortForm(auxCl)
                            .equals(Class)) {
                        // the domain is equivalent to the given
                        names.add(queryEngine.getShortFormProvider()
                                .getShortForm(
                                        (OWLEntity) aux
                                                .getRepresentativeElement()));
                    }
                }
            }
        }

        return names;
    }

    /**
     * Gets instances of the given class
     *
     * @param Class
     * @return list of names
     */
    public List<String> getNamesInstances(String Class) {
        Set<OWLNamedIndividual> instances = queryEngine.getInstances(Class,
                false);
        List<String> names = new ArrayList<String>();

        for (OWLNamedIndividual aux : instances) {
            names.add(queryEngine.getShortFormProvider().getShortForm(aux));
        }

        return names;
    }

    /**
     * Checks the consistency of the ontology
     *
     * @return boolean (true if consistent)
     */
    public Boolean checkConsistency() {
        return reasoner.isConsistent();
    }

    @SuppressWarnings("unused")
    public void test() {
        OWLClass Class_user = factory.getOWLClass(IRI.create(ontology
                .toString() + "#User"));
        OWLIndividual Individual_Prajit = factory.getOWLNamedIndividual(IRI
                .create(ontology.toString() + "#Prajit"));
        OWLIndividual Individual_context = factory.getOWLNamedIndividual(IRI
                .create(ontology.toString() + "#Context_Prajit"));
        // http://www.semanticweb.org/ontologies/2012/10/zaragoza.owl#user
        OWLDataProperty Dataproperty_hasName = factory.getOWLDataProperty(IRI
                .create(ontology.toString() + "#hasName"));
        OWLObjectProperty Objectproperty_hasContext = factory
                .getOWLObjectProperty(IRI.create(ontology.toString()
                        + "#hasContext"));

        List<OWLOntologyChange> changeList = new ArrayList<OWLOntologyChange>();
        changeList.add(new AddAxiom(ontology, factory
                .getOWLClassAssertionAxiom(Class_user, Individual_Prajit)));
        // changeList.add(new AddAxiom(ontology, factory
        // .getOWLObjectPropertyAssertionAxiom(Objectproperty_hasContext,
        // Individual_Prajit, Individual_context)));
        manager.applyChanges(changeList);
    }

    public void doTest(int reasoner_type, int ontology_id) throws Exception {

        // String aQuery = "yearValue value 1998";
        // String aQuery =
        // "hasDateTime some dateTime[>=\"2002-10-10T17:10:00Z\"^^dateTime]";
        // String aQuery = "hasDate some date[>=\"2002-10-10+13:00\"^^date]";
        // Log.i(Mithril.getDebugTag(), String.format("Querying [%s]...", aQuery));
        // startTime = System.nanoTime();
        // for (OWLClass clase : queryEngine.getSuperClasses(aQuery, false)) {
        // Log.i(Mithril.getDebugTag(), clase.toString());
        // }
        // for (OWLNamedIndividual individual : queryEngine.getInstances(aQuery,
        // false)) {
        // Log.i(Mithril.getDebugTag(), individual.toString());
        // }
        // finishTime = System.nanoTime();
        // Log.i(Mithril.getDebugTag(), "... has runned for " + (finishTime - startTime) / 1e9 +
        // " seconds");
    }

    public void finish() {
        queryEngine.dispose();
        queryEngine = null;
    }

    public Set<OWLClass> querySubClasses(DLQueryEngine queryEngine, String query)
            throws Exception {
        return queryEngine.getSubClasses(query, true);
    }

    public OWLReasoner createOWLReasoner(OWLOntology ontology)
            throws IllegalArgumentException {
        // jFact-1.0
        // return new JFactReasoner(ontology, new
        // JFactReasonerConfiguration(), BufferingMode.NON_BUFFERING);
        return new JFactFactory().createNonBufferingReasoner(ontology);
    }

    public DLQueryEngine createDLQueryEngine(OWLReasoner reasoner)
            throws IllegalArgumentException {
        if (reasoner == null) {
            throw new IllegalArgumentException("OWLReasoner is null");
        }
        return new DLQueryEngine(reasoner, new SimpleShortFormProvider());
    }

//	public UserContextInformation enrichContext(UserContextInformation info,
//			String BTContext) {
//
//		UserContextInformation newContext = new UserContextInformation();
//		newContext.id = info.id;
//
//		Log.d(Mithril.getDebugTag(), "*-*-*-*-" + info.toString());
//
//		if (info.location == null) {
//			info.location = "";
//		}
//		if (info.activity == null) {
//			info.activity = "";
//		} else {
//			info.activity += "Ind";
//		}
//
//		// you cannot run network methods on the main thread but I don't care
//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//				.permitAll().build();
//		StrictMode.setThreadPolicy(policy);
//
//		// first I extract facts from my ontology
////		Log("--> 0.1)Extracting facts from ontology", newContext);
//
////		FactCollection fcOwn = extractFactsOntology();
//
//		// let me show you the result
////		Log("\nThis is the content of the json file:", newContext);
////		Log(fcOwn.toString(), newContext);
//
//		// I can even send this facts to other users with JSON
//		// we use gson for JSON management
//		Gson gson = new Gson();
//
//		// String jsonGenerated = gson.toJson(fcOwn);
//		// Log("JSON Generated:\n" + jsonGenerated, newContext);
//
//		// now I add the facts from the interface
//		Log("--> 0.2)Extracting facts from GUI", newContext);
//
//		FactCollection fcGUI = extractFactsGUI(info);
//
//		// let me show you the result
//		Log("\nThis is the content of the json file:", newContext);
//		Log(fcGUI.toString(), newContext);
//
//		// jsonGenerated = gson.toJson(fcOwn);
//		// Log("JSON Generated:\n" + jsonGenerated, newContext);
//
//		// now I will get more facts from the network
//
//		Log("\n--> 1)Parsing json file: " + url, newContext);
//
//		InputStream source = retrieveStream(url);
//		Reader reader = new InputStreamReader(source);
//
//		FactCollection r = gson.fromJson(reader, FactCollection.class);
//
//		@SuppressWarnings("unused")
//		List<ContextFact> cfs = r.contextFacts;
//
//		// let me show you the result
//		Log("\nThis is the content of the json file:", newContext);
//		Log(r.toString(), newContext);
//
//		// now I will get more facts from BT
//
//		Log("\n--> 1)Parsing json file: " + "Bluetooth", newContext);
//
//		FactCollection fcBT = gson.fromJson(BTContext, FactCollection.class);
//
//		// let me show you the result
//		Log("\nThis is the content of the json file:", newContext);
//		Log(fcBT.toString(), newContext);
//
//		List<ContextFact> finalcfs= new ArrayList<ContextFact>();
//		
////		// adding my own contextfact from ontology
////		cfs.addAll(fcOwn.contextFacts);
////		// adding my own contextfact from GUI
////		cfs.addAll(fcGUI.contextFacts);
//		
//		// adding contextfact from URL
////		finalcfs.addAll(cfs);
//		// adding my own contextfact from ontology
////		finalcfs.addAll(fcOwn.contextFacts);
//		// adding my own contextfact from GUI
//		finalcfs.addAll(fcGUI.contextFacts);
//		// adding contextfacts from BT
//		finalcfs.addAll(fcBT.contextFacts);
//
//		Log("\n--> 2)Extracting locations and confidences from json...",
//				newContext);
//
//		// lets do some magic with the facts parsed
//
//		// first extract some locations
//		ArrayList<String> locations = new ArrayList<String>();
//		ArrayList<Double> confidencesLoc = new ArrayList<Double>();
//
//		// and some activities
//		ArrayList<String> activities = new ArrayList<String>();
//		ArrayList<Double> confidencesAct = new ArrayList<Double>();
//
//		String user = info.id;
//		// for every user we check first facts related to his location and
//		// activity
//
//		Log("\n\tChecking facts for: " + user, newContext);
//		for (ContextFact cf : finalcfs) {
//			List<Fact> facts = cf.facts;
//			for (Fact f : facts) {
//				//is this a fact about the user??
//				//otherwise I will lower the confidence
//				double pen=1.0;
//				if(!f.subject.equalsIgnoreCase(user)){
//					pen=2.0;
//				}
//				
//				// is this a location fact?
//				if (f.predicate.equalsIgnoreCase("hasLocation")) {
////				if (f.subject.equalsIgnoreCase(user)
////						&& f.predicate.equalsIgnoreCase("hasLocation")) {
//
//					locations.add(f.object);
//
//					// for now the confidence is the average of the confidence
//					// of
//					// supporting facts
//					List<Support> support = cf.support;
//
//					double confidence = 0.0;
//					if (!support.isEmpty()) {
//						for (Support s : support) {
//							confidence += s.confidence;
//						}
//						confidence /= support.size();
//					}
//
//					// lets use also the confidence on the source
//					confidence += cf.confidence;
//					confidence /= 2;
//
//					confidencesLoc.add(confidence/pen);
////				} else if (f.subject.equalsIgnoreCase(user)
////						&& f.predicate.equalsIgnoreCase("hasActivity")) {
//				} else if (f.predicate.equalsIgnoreCase("hasActivity")) {
//					// is this an activity fact about the user?
//					activities.add(f.object);
//
//					// for now the confidence is the average of the confidence
//					// of
//					// supporting facts
//					List<Support> support = cf.support;
//
//					double confidence = 0.0;
//					if (!support.isEmpty()) {
//						for (Support s : support) {
//							confidence += s.confidence;
//						}
//						confidence /= support.size();
//					}
//
//					// lets use also the confidence on the source
//					confidence += cf.confidence;
//					confidence /= 2;
//
//					confidencesAct.add(confidence/pen);
//				}
//			}
//		}
//
//		Log(locations.toString(), newContext);
//		Log(confidencesLoc.toString(), newContext);
//
//		Log(activities.toString(), newContext);
//		Log(confidencesAct.toString(), newContext);
//
//		if (!locations.isEmpty()) {
//
//			Log("\n--> 3)Computing the confidence for locations taking semantics into account...",
//					newContext);
//			// lets compute the confidence taking into account semantics
//			TreeMap<String, Double> tm = checkLocationFacts(locations,
//					confidencesLoc, user);
//			Log(tm.toString(), newContext);
//
//			// lets check for inconsistencies
//			Log("\nI will add the following axioms:\n", newContext);
//			String aux = checkConsistencyLocation(tm, user, "hasLocation");
//			Log(aux, newContext);
//
//			newContext.location = aux;
//		}
//
//		if (!activities.isEmpty()) {
//			Log("\n--> 3)Computing the confidence for activities taking semantics into account...",
//					newContext);
//			// lets compute the confidence taking into account semantics
//			TreeMap<String, Double> tm = checkActivityFacts(activities,
//					confidencesAct, user);
//			Log(tm.toString(), newContext);
//
//			// for now we do not check inconsistencies with activities
//			int i = 0;
//
//			for (Map.Entry e : tm.entrySet()) {
//				if (i != 0)
//					newContext.activity += " & ";
//				newContext.activity += e.getKey() + "("
//						+ Double.toString((Double) e.getValue()) + ")";
//				i++;
//			}
//
//			// for now we do not check inconsistencies with activities
//			// for (int i = 0; i < activities.size(); i++) {
//			// if (i != 0)
//			// newContext.activity += " & ";
//			// newContext.activity += activities.get(i) + "(" +
//			// confidencesAct.get(i) + ")";
//			// }
//		}
//
//		return newContext;
//	}

//	public FactCollection extractFactsGUI(UserContextInformation infoUser) {
//
//		FactCollection fc = new FactCollection();
//
//		fc.contextFacts = new ArrayList<ContextFact>();
//
//		if (!infoUser.location.equalsIgnoreCase("")) {
//			// extracting location facts
//			ContextFact loccf = new ContextFact();
//			loccf.source = "I";
//			loccf.confidence = 1.0;
//			loccf.device_name = "myDevice";
//
//			Fact locf = new Fact();
//			locf.subject = infoUser.id;
//			locf.predicate = "hasLocation";
//			locf.object = infoUser.location;
//			loccf.facts = new ArrayList<Fact>();
//			loccf.facts.add(locf);
//
//			loccf.support = new ArrayList<Support>();
//
//			// add supports
//			for (Double aux : infoUser.sfLocation) {
//				Support s = new Support();
//				s.confidence = aux;
//
//				loccf.support.add(s);
//			}
//
//			fc.contextFacts.add(loccf);
//		}
//
//		if (!infoUser.activity.equalsIgnoreCase("")) {
//			// extracting activity facts
//			ContextFact actcf = new ContextFact();
//			actcf.source = "I";
//			actcf.confidence = 1.0;
//			actcf.device_name = "myDevice";
//
//			Fact f = new Fact();
//			f.subject = infoUser.id;
//			f.predicate = "hasActivity";
//			f.object = infoUser.activity;
//			actcf.facts = new ArrayList<Fact>();
//			actcf.facts.add(f);
//
//			actcf.support = new ArrayList<Support>();
//
//			// add supports
//			for (Double aux : infoUser.sfActivity) {
//				Support s = new Support();
//				s.confidence = aux;
//
//				actcf.support.add(s);
//			}
//
//			fc.contextFacts.add(actcf);
//		}
//
//		return fc;
//	}
//
//	public static void Log(String text, UserContextInformation infoUser) {
//
//		Log.e(Mithril.getDebugTag(), text);
//
//		infoUser.info += text;
//
//	}

    @SuppressWarnings("unused")
//	private InputStream retrieveStream(String url) {
//
//		DefaultHttpClient client = new DefaultHttpClient();
//
//		HttpGet getRequest = new HttpGet(url);
//
//		try {
//
//			HttpResponse getResponse = client.execute(getRequest);
//			final int statusCode = getResponse.getStatusLine().getStatusCode();
//
//			if (statusCode != HttpStatus.SC_OK) {
//				Log.w(getClass().getSimpleName(), "Error " + statusCode
//						+ " for URL " + url);
//				return null;
//			}
//
//			HttpEntity getResponseEntity = getResponse.getEntity();
//			return getResponseEntity.getContent();
//
//		} catch (IOException e) {
//			getRequest.abort();
//			Log.w(getClass().getSimpleName(), "Error for URL " + url, e);
//		}
//
//		return null;
//
//	}

    /**
     * Adds a fact to the ontology
     *
     * @param f
     */
    // public void addFact(Fact f){
    //
    // //for now we consider that the subject and object are individuals
    // //and the predicate is an object property
    //
    // OWLIndividual Individual_subject = factory.getOWLNamedIndividual(IRI
    // .create(ontology.toString() + "#"+f.subject));
    //
    // OWLObjectProperty Objectproperty_predicate = factory
    // .getOWLObjectProperty(IRI.create(ontology.toString()
    // + "#"+f.predicate));
    //
    // OWLIndividual Individual_object = factory.getOWLNamedIndividual(IRI
    // .create(ontology.toString() + "#"+f.object));
    //
    //
    // List<OWLOntologyChange> changeList = new ArrayList<OWLOntologyChange>();
    //
    // changeList.add(new AddAxiom(ontology, factory
    // .getOWLObjectPropertyAssertionAxiom(Objectproperty_predicate,
    // Individual_subject, Individual_object)));
    //
    // manager.applyChanges(changeList);
    // }

    public TreeMap<String, Double> checkLocationFacts(
            ArrayList<String> locations, ArrayList<Double> confidences,
            String user) {

        // Creating a set of Locations (without repetitions)
        HashSet<String> locationsSet = new HashSet<String>(locations);

        // and a map for the voting system
        HashMap<String, Double> locationsVote = new HashMap<String, Double>();
        ValueComparator bvc = new ValueComparator(locationsVote);

        for (String locSet : locationsSet) {
            // Who is supporting this location?
            Double supValue = 0.0;
            Set<OWLNamedIndividual> containers = queryEngine.getInstances(
                    "isIn value " + locSet, false);
            int i = 0;
            for (String locNew : locations) {
                // if the location is equivalent or a subclass then is a
                // supporter
                Log.i(MithrilApplication.getDebugTag(), "--CHECKING: " + locSet + " vs " + locNew);
                if (locSet == locNew) {
                    Log.i(MithrilApplication.getDebugTag(), "-->Equivalent!");
                    if (supValue != 0.0)
                        supValue = (supValue + confidences.get(i)) / 2.0;
                    else
                        supValue = supValue + confidences.get(i);
                } else if (isIn(locNew, containers)) {
                    Log.i(MithrilApplication.getDebugTag(), "-->is In!");
                    if (supValue != 0.0)
                        supValue = (supValue + confidences.get(i)) / 2.0;
                    else
                        supValue = supValue + confidences.get(i);
                }
                i++;
            }
            locationsVote.put(locSet, supValue);
        }

        // sorting the map
        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
        sorted_map.putAll(locationsVote);

        Log.i(MithrilApplication.getDebugTag(), "results: " + sorted_map);

        return sorted_map;
    }

    public TreeMap<String, Double> checkActivityFacts(
            ArrayList<String> activities, ArrayList<Double> confidences,
            String user) {

        // Creating a set of Locations (without repetitions)
        HashSet<String> activitiesSet = new HashSet<String>(activities);

        // and a map for the voting system
        HashMap<String, Double> activitiesVote = new HashMap<String, Double>();
        ValueComparator bvc = new ValueComparator(activitiesVote);

        for (String actSet : activitiesSet) {
            // Who is supporting this activity?
            Double supValue = 0.0;

            Log.i(MithrilApplication.getDebugTag(), "*-*-*-" + actSet);
            OWLNamedIndividual indvA = (OWLNamedIndividual) bidiShortFormProvider
                    .getEntity(actSet);
            Log.d(MithrilApplication.getDebugTag(), "-*-*-*-*" + actSet + ", " + indvA);
            OWLClass classA = indvA.getTypes(ontology).iterator().next()
                    .asOWLClass();
            String nameClassA = bidiShortFormProvider.getShortForm(classA);

            List<String> containers = getNamesSubClasses(nameClassA);

            int i = 0;
            for (String actNew : activities) {
                // if the activity is equivalent or a subclass then is a
                // supporter

                OWLNamedIndividual indvB = (OWLNamedIndividual) bidiShortFormProvider
                        .getEntity(actNew);
                OWLClass classB = indvB.getTypes(ontology).iterator().next()
                        .asOWLClass();
                String nameClassB = bidiShortFormProvider.getShortForm(classB);

                Log.i(MithrilApplication.getDebugTag(), "--CHECKING: " + actSet + " vs " + actNew);
                if (nameClassA == nameClassB) {
                    Log.i(MithrilApplication.getDebugTag(), "-->Equivalent!");
                    if (supValue != 0.0)
                        supValue = (supValue + confidences.get(i)) / 2.0;
                    else
                        supValue = supValue + confidences.get(i);
                } else if (isIn(nameClassB, containers)) {
                    Log.i(MithrilApplication.getDebugTag(), "-->is In!");
                    if (supValue != 0.0)
                        supValue = (supValue + confidences.get(i)) / 2.0;
                    else
                        supValue = supValue + confidences.get(i);
                }
                i++;
            }
            activitiesVote.put(actSet, supValue);
        }

        // sorting the map
        TreeMap<String, Double> sorted_map = new TreeMap<String, Double>(bvc);
        sorted_map.putAll(activitiesVote);

        Log.i(MithrilApplication.getDebugTag(), "results: " + sorted_map);

        return sorted_map;
    }

    public String checkConsistencyLocation(TreeMap<String, Double> tm,
                                           String subject, String predicate) {

        // first we generate the axioms for subject and predicate
        OWLIndividual Individual_subject = (OWLIndividual) bidiShortFormProvider
                .getEntity(subject);
        OWLObjectProperty Objectproperty_predicate = (OWLObjectProperty) bidiShortFormProvider
                .getEntity(predicate);

        // now we try the object with the highest confidence
        String obj = tm.firstKey();

        // Create axioms and materialize them in the ontology
        // then use reasoner to check consistency
        // **We are not using this right now...
        // OWLIndividual Individual_object = (OWLIndividual)
        // bidiShortFormProvider.getEntity(obj);
        //
        // List<OWLOntologyChange> changeList = new
        // ArrayList<OWLOntologyChange>();
        //
        // changeList.add(new AddAxiom(ontology,
        // factory.getOWLObjectPropertyAssertionAxiom(Objectproperty_predicate,
        // Individual_subject, Individual_object)));
        //
        // Log.i(Mithril.getDebugTag(), changeList.toString());
        //
        // manager.applyChanges(changeList);

        // Pellet can explain inconsistencies but we are using Jfact...
        // if(!checkConsistency()){
        //
        // GlassBoxExplanation.setup();
        // SingleExplanationGenerator eg = new GlassBoxExplanation(ontology,
        // PelletReasonerFactory.getInstance());
        // try {
        // for (OWLAxiom ax : eg.getExplanation(factory.getOWLThing())) {
        // System.out.println(ax);
        // }
        // } catch (OWLRuntimeException ex) {
        // System.out.println("cannot explain: " + ex.getMessage());
        // }
        // }

        // remove the axiom
        // manager.removeAxiom(ontology, changeList.get(0).getAxiom());

        // **Instead we ''manually'' check consistency using the reasoner to
        // obtain
        // **if a location is inside another one
        List<OWLOntologyChange> finalChangeList = new ArrayList<OWLOntologyChange>();
        Set<OWLNamedIndividual> containers = queryEngine.getInstances(
                "isIn value " + obj, false);
        containers.addAll(queryEngine.getInstances("contains value " + obj,
                false));

        String newLoc = obj;

        for (String obj2 : tm.descendingKeySet()) {
            OWLIndividual Individual_object = (OWLIndividual) bidiShortFormProvider
                    .getEntity(obj2);
            if (obj.equalsIgnoreCase(obj2)) {
                // we check if this object is consistent with the current
                // ontology
                OWLNamedIndividual prevLoc = null;
                try {
                    prevLoc = queryEngine
                            .getInstances("holds value " + subject, true)
                            .iterator().next();
                } catch (Exception e) {

                }
                if (prevLoc != null) {
                    Log.i(MithrilApplication.getDebugTag(), "-*-*-*-*>>PrevLoc:" + prevLoc);
                    if (!isIn(bidiShortFormProvider.getShortForm(prevLoc),
                            containers)) {
                        // it was not consistent so we have to remove the
                        // previous axiom
                        // from ontology
                        finalChangeList.add(new RemoveAxiom(ontology, factory
                                .getOWLObjectPropertyAssertionAxiom(
                                        Objectproperty_predicate,
                                        Individual_subject, prevLoc)));
                    }
                }

                finalChangeList.add(new AddAxiom(ontology, factory
                        .getOWLObjectPropertyAssertionAxiom(
                                Objectproperty_predicate, Individual_subject,
                                Individual_object)));

            } else if (isIn(obj2, containers)) {
                // we check if obj and obj2 are consistent (that is obj2
                // contains obj1)
                finalChangeList.add(new AddAxiom(ontology, factory
                        .getOWLObjectPropertyAssertionAxiom(
                                Objectproperty_predicate, Individual_subject,
                                Individual_object)));
                newLoc = obj2;
            }
        }

        return newLoc;

        // return finalChangeList;
    }

//	public FactCollection extractFactsOntology() {
//
//		FactCollection fc = new FactCollection();
//		fc.contextFacts = new ArrayList<ContextFact>();
//
//		// extracting users in the ontology
//		List<String> users = getUsers();
//
//		Log.i(Mithril.getDebugTag(), users.toString());
//
//		for (String user : users) {
//
//			// extracting location facts
//			try {
//				ContextFact cf = new ContextFact();
//				cf.source = "I";
//				cf.confidence = 1.0;
//				cf.device_name = "myDevice";
//
//				String locUser = bidiShortFormProvider.getShortForm(queryEngine
//						.getInstances("holds value " + user, true).iterator()
//						.next());
//				Fact f = new Fact();
//				f.subject = user;
//				f.predicate = "hasLocation";
//				f.object = locUser;
//				cf.facts = new ArrayList<Fact>();
//				cf.facts.add(f);
//
//				// add support
//				Support s = new Support();
//				s.confidence = 0.5;
//
//				cf.support = new ArrayList<Support>();
//				cf.support.add(s);
//
//				fc.contextFacts.add(cf);
//
//			} catch (Exception e) {
//				Log.i(Mithril.getDebugTag(), "I don't have location facts for user: " + user);
//			}
//
//			// extracting activity facts
//			try {
//				ContextFact cf = new ContextFact();
//				cf.source = "I";
//				cf.confidence = 1.0;
//				cf.device_name = "myDevice";
//
//				String actUser = bidiShortFormProvider.getShortForm(queryEngine
//						.getInstances("isPerformedBy value " + user, true)
//						.iterator().next());
//				Fact f = new Fact();
//				f.subject = user;
//				f.predicate = "hasActivity";
//				f.object = actUser;
//				cf.facts = new ArrayList<Fact>();
//				cf.facts.add(f);
//
//				// add support
//				Support s = new Support();
//				s.confidence = 0.5;
//
//				cf.support = new ArrayList<Support>();
//				cf.support.add(s);
//
//				fc.contextFacts.add(cf);
//
//			} catch (Exception e) {
//				Log.i(Mithril.getDebugTag(), "I don't have activity facts for user: " + user);
//			}
//		}
//
//		return fc;
//	}

    public List<String> getUsers() {
        List<String> users = new ArrayList<String>();
        for (OWLNamedIndividual aux : queryEngine.getInstances("User", true)) {
            users.add(bidiShortFormProvider.getShortForm(aux));
        }

        return users;
    }

    public boolean isIn(String value, Set<OWLNamedIndividual> individuals) {
        if (individuals.isEmpty())
            return false;

        OWLIndividual individualValue = (OWLIndividual) bidiShortFormProvider
                .getEntity(value);

        // Log.i(Mithril.getDebugTag(), individualValue.toString());
        // Log.i(Mithril.getDebugTag(), individuals.toString());
        // Log.i(Mithril.getDebugTag(), individuals.iterator().next().toString());

        return individuals.contains(individualValue);
    }

    public boolean isIn(String value, List<String> containers) {

        return containers.contains(value);
    }

    class ValueComparator implements Comparator<String> {

        Map<String, Double> base;

        public ValueComparator(Map<String, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with
        // equals.
        public int compare(String a, String b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

}