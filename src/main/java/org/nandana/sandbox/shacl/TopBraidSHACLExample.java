package org.nandana.sandbox.shacl;


import org.apache.jena.graph.Graph;
import org.apache.jena.graph.compose.MultiUnion;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.arq.SHACLFunctions;
import org.topbraid.shacl.constraints.ModelConstraintValidator;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.spin.arq.ARQFactory;

import java.net.URI;
import java.util.UUID;

public class TopBraidSHACLExample {

    private static final Logger logger = LoggerFactory.getLogger(TopBraidSHACLExample.class);

    public static void main(String[] args) throws InterruptedException {



        //Load the data into a model
        Model dataModel = ModelFactory.createDefaultModel();
        dataModel.read("personData.ttl");


        //Load the shape into a model
        Model shapeModel = ModelFactory.createDefaultModel();
        shapeModel.read("personShape.ttl");

        // Load the shapes Model (here, includes the dataModel because that has shape definitions too)
        Model shaclModel = SHACLSystemModel.getSHACLModel();
        MultiUnion unionGraph = new MultiUnion(new Graph[] {
                shaclModel.getGraph(),
                dataModel.getGraph(),
                shapeModel.getGraph()
        });
        Model shapesModel = ModelFactory.createModelForGraph(unionGraph);

        // Make sure all sh:Functions are registered
        SHACLFunctions.registerFunctions(shapesModel);



        // Create Dataset that contains both the main query model and the shapes model
        // (here, using a temporary URI for the shapes graph)
        URI shapesGraphURI = URI.create("urn:x-shacl-shapes-graph:" + UUID.randomUUID().toString());
        Dataset dataset = ARQFactory.get().getDataset(dataModel);
        dataset.addNamedModel(shapesGraphURI.toString(), shapesModel);

        Model results = new ModelConstraintValidator().validateModel(dataset, shapesGraphURI, null, true, null, null).getModel();
        addPrefixes(results);
        System.out.println(ModelPrinter.get().print(results));

    }

    private static void addPrefixes(Model model) {
        if (model == null) {
          return;
        }

        model.setNsPrefix("sh", "http://www.w3.org/ns/shacl#");
    }
}
