package org.nandana.sandbox.topbraid;

/**
 * Copyright 2016-2017 Ontology Engineering Group, Universidad Politécnica de Madrid, Spain
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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

/***
 * A simple example of using TopBraid SHACL API for validating RDF data with SHACL shapes
 * @author Nandana Mihindulusooriya
 * @version 0.0.1
 * @since 12/02/2017
 */
public class TopBraidShaclExample {

    private static final Logger logger = LoggerFactory.getLogger(TopBraidShaclExample.class);

    public static void main(String[] args) throws InterruptedException {

        //Load the person data into a model
        Model personData = ModelFactory.createDefaultModel();
        personData.read("personData.ttl");

        //Load the person shape into a model
        Model personShape = ModelFactory.createDefaultModel();
        personShape.read("personShape.ttl");

        // Load the topbraid Model (this includes the default SHACL related definitions that are used
        // by the TopBraid API to support SHACL.
        Model shaclModel = SHACLSystemModel.getSHACLModel();

        //Get the union of our shapes and default ones.
        MultiUnion unionGraph = new MultiUnion(new Graph[] {
                shaclModel.getGraph(),
                personShape.getGraph()
        });
        Model shapesModel = ModelFactory.createModelForGraph(unionGraph);

        // Make sure all sh:Functions are registered
        SHACLFunctions.registerFunctions(shapesModel);

        // Create Dataset that contains both the main query model and the shapes model
        // (here, using a temporary URI for the shapes graph)
        URI shapesGraphURI = URI.create("urn:topbraid-shapes-graph:" + UUID.randomUUID().toString());
        Dataset dataset = ARQFactory.get().getDataset(personData);
        dataset.addNamedModel(shapesGraphURI.toString(), shapesModel);

        Model results = new ModelConstraintValidator().validateModel(dataset, shapesGraphURI, null, true, null, null).getModel();
        addPrefixes(results);

        //Print the validiation result
        System.out.println(ModelPrinter.get().print(results));

    }

    /***
     * This method adds prefixes to a given model to make the serializations more readable
     * @param model the Jena model to add the prefixes
     */
    private static void addPrefixes(Model model) {
        if (model == null) {
          return;
        }
        model.setNsPrefix("sh", "http://www.w3.org/ns/topbraid#");
    }
}
