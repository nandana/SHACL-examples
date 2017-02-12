package org.nandana.sandbox.shacl;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.topbraid.shacl.arq.SHACLFunctions;
import org.topbraid.shacl.vocabulary.SH;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.util.SystemTriples;

import java.io.InputStream;

public class SHACLSystemModel {

    private static Model shaclModel;

    public static Model getSHACLModel() {
        if(shaclModel == null) {

            shaclModel = JenaUtil.createDefaultModel();

            InputStream shaclTTL = SHACLSystemModel.class.getResourceAsStream("/etc/shacl.ttl");
            shaclModel.read(shaclTTL, SH.BASE_URI, FileUtils.langTurtle);

            InputStream dashTTL = SHACLSystemModel.class.getResourceAsStream("/etc/dash.ttl");
            shaclModel.read(dashTTL, SH.BASE_URI, FileUtils.langTurtle);

            InputStream toshTTL = SHACLSystemModel.class.getResourceAsStream("/etc/tosh.ttl");
            shaclModel.read(toshTTL, SH.BASE_URI, FileUtils.langTurtle);

            InputStream shacljsTTL = SHACLSystemModel.class.getResourceAsStream("/etc/shacljs.ttl");
            shaclModel.read(shacljsTTL, SH.BASE_URI, FileUtils.langTurtle);

            shaclModel.add(SystemTriples.getVocabularyModel());

            SHACLFunctions.registerFunctions(shaclModel);
        }
        return shaclModel;
    }




}
