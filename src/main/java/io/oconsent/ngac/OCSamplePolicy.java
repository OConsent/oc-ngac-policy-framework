package io.oconsent.ngac;

/*
* OConsent - Open Consent Protocol
* Subhadip Mitra, <dev@subhadipmitra.com>
* 24-11-2020
*
* */
import gov.nist.csd.pm.decider.*;
import gov.nist.csd.pm.exceptions.PMException;
import gov.nist.csd.pm.graph.*;
import gov.nist.csd.pm.graph.model.nodes.Node;
import gov.nist.csd.pm.graph.model.nodes.NodeType;
import gov.nist.csd.pm.prohibitions.model.Prohibition;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
    The following snippet shows how an NGAC policy may be defined in Java, with Data
    Subjects (DS),
    Data Controllers (DC) and Data Processors (DP).
    It also shows how multiple data assets may be linked to multiple Consent Agreements
    and DC/DP.
*/
public class OCSamplePolicy {
    protected OCSamplePolicy() {
        // default constructor
    }

    public static class Builder {
        public static OCSamplePolicy build() throws PMException {
            Random r = new Random();
            Graph g = new MemGraph();

            // Create Users
            Node oconsentUserM = g.createNode( r.nextLong(), "John Doe", NodeType.U, null);

            // Create Admin User and attributes
            Node admin = g.createNode( r.nextLong(), "OConsent Admin", NodeType.UA, null);

            // Make user and admin
            g.assign(oconsentUserM.getID(), admin.getID());

            // Create objects
            Node dataAsset1 = g.createNode(r.nextLong(), "DataAsset1", NodeType.O, null);
            Node dataAsset2 = g.createNode(r.nextLong(), "DataAsset2", NodeType.O, null);
            Node dataAsset3 = g.createNode(r.nextLong(), "DataAsset2", NodeType.O, null);

            // Create the OConsentPolicy Class
            Node dataAssetOConsentPolicy = g.createNode( r.nextLong(), "DataAsset Access OConsentPolicy", NodeType.PC, null);

            // Create the data subject attribute
            Node dataSubjectX = g.createNode( r.nextLong(), "DataSubjects", NodeType.OA, null);

            // Create Data Controller A and Data Processor B
            Node dataControllerA = g.createNode( r.nextLong(), "DataController", NodeType.OA, null);
            g.assign(dataControllerA.getID(), dataSubjectX.getID());

            Node dataProcessorB = g.createNode( r.nextLong(), "DataProcessor", NodeType.OA, null);
            g.assign(dataProcessorB.getID(), dataSubjectX.getID());


            // Assigning DataAssets to DataSubjects
            g.assign(dataAsset1.getID(), dataControllerA.getID());
            g.assign(dataAsset2.getID(), dataProcessorB.getID());
            g.assign(dataAsset3.getID(), dataProcessorB.getID());


            // Create agreements - these are tagged to Hashed Signed Agreements of the OConsent Platform.
            Node consentAgreementGlobal = g.createNode( r.nextLong(), "ConsentAgreements", NodeType.OA, null);
            Node agreementMarketing_1 = g.createNode( r.nextLong(), "agreementMarketing", NodeType.OA, null);
            g.assign(agreementMarketing_1.getID(), consentAgreementGlobal.getID());

            Node agreementAnalytics_1 = g.createNode( r.nextLong(), "agreementAnalytics", NodeType.OA, null);
            g.assign(agreementAnalytics_1.getID(), consentAgreementGlobal.getID());


            // Assign Data Assets to Consent Agreements
            g.assign(dataAsset1.getID(), agreementMarketing_1.getID());
            g.assign(dataAsset2.getID(), agreementMarketing_1.getID());
            g.assign(dataAsset3.getID(), agreementAnalytics_1.getID());


            // Assign the `Data Subject` and `Consent Agreements` objects attribute to
            // the `OConsentPolicy` policy class node.
            g.assign(dataSubjectX.getID(), dataAssetOConsentPolicy.getID());
            g.assign(consentAgreementGlobal.getID(), dataAssetOConsentPolicy.getID());


            //This will give user read and write on `Data Processor` and `dataControllerA`
            g.associate(admin.getID(), dataControllerA.getID(), new HashSet<>(Arrays.asList("r", "w")));
            g.associate(admin.getID(), dataProcessorB.getID(), new HashSet<>(Arrays.asList("r", "w")));


            //Create a NGAC Prohibition (for demonstration)
            /*
                Prohibition prohibition = new Prohibition();

                for (Node node : Arrays.asList(agreementAnalytics_1, oconsentUserM)) {
                    prohibition.addNode(Prohibition.Node.class node);
                }
            */

            Decider dec = new PReviewDecider(g);
            Set<String> permissions = dec.listPermissions(oconsentUserM.getID(),dataAsset1.getID());

            assertTrue(permissions.contains("r"));
            assertTrue(permissions.contains("w"));

            return new OCSamplePolicy();
        }
    }
}