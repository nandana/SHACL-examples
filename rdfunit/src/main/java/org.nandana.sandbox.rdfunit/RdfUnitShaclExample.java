package org.nandana.sandbox.rdfunit;

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

import org.aksw.rdfunit.enums.TestCaseExecutionType;
import org.aksw.rdfunit.io.reader.RdfModelReader;
import org.aksw.rdfunit.io.reader.RdfReader;
import org.aksw.rdfunit.io.reader.RdfReaderException;
import org.aksw.rdfunit.io.reader.RdfReaderFactory;
import org.aksw.rdfunit.model.interfaces.TestSuite;
import org.aksw.rdfunit.model.interfaces.results.TestExecution;
import org.aksw.rdfunit.sources.SchemaSource;
import org.aksw.rdfunit.sources.SchemaSourceFactory;
import org.aksw.rdfunit.tests.generators.ShaclTestGenerator;
import org.aksw.rdfunit.validate.wrappers.RDFUnitStaticValidator;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/***
 * A simple example of using RDF Unit for validating RDF data with SHACL shapes
 * @author Nandana Mihindulusooriya
 * @version 0.0.1
 * @since 12/02/2017
 */
public class RdfUnitShaclExample {

    public static void main(String[] args) {

        //Load the person data into a model
        Model personData = ModelFactory.createDefaultModel();
        personData.read("personData.ttl");

        TestSuite testSuite = createTestSuiteWithShacl("/personShape.ttl");

        TestExecution te = RDFUnitStaticValidator.validate(TestCaseExecutionType.shaclFullTestCaseResult, personData, testSuite);


        te.getTestCaseResults().stream().forEach(result -> {
            throw new IllegalArgumentException("resource: " + result.getTestCaseUri() + " failed with message: " + result.getMessage());
        });

    }



    private static TestSuite createTestSuiteWithShacl(String schemaSource) {
        RdfReader ontologyShaclReader = null;
        try {
            ontologyShaclReader = new RdfModelReader(RdfReaderFactory.createResourceReader(schemaSource).read());
        } catch (RdfReaderException e) {
            throw new IllegalArgumentException(e);
        }
        SchemaSource ontologyShaclSource = SchemaSourceFactory.createSchemaSourceSimple("tests", "http://rdfunit.aksw.org", ontologyShaclReader);
        return new TestSuite(new ShaclTestGenerator().generate(ontologyShaclSource));
    }
}
