/*
 * Copyright (C) 2010-2011  "Bio4j"
 *
 * This file is part of Bio4j
 *
 * Bio4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.ohnosequences.bio4j.tools.taxonomy;

import com.ohnosequences.bio4j.neo4j.model.nodes.ncbi.NCBITaxonNode;
import com.ohnosequences.bio4j.neo4j.model.util.Bio4jManager;
import com.ohnosequences.bio4j.neo4j.model.util.NodeRetriever;
import com.ohnosequences.bio4j.tools.algo.TaxonomyAlgo;
import com.ohnosequences.util.Executable;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class LowestCommonAncestorTest implements Executable{
    
    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }
    
    public static void main(String args[]){
        
        LinkedList<NCBITaxonNode> nodes = new LinkedList<NCBITaxonNode>();
        
        Bio4jManager bio4jManager = new Bio4jManager(args[0]);
        NodeRetriever nodeRetriever = new NodeRetriever(bio4jManager);
                
        for (int i = 1; i < args.length; i++) {
            nodes.add(nodeRetriever.getNCBITaxonByTaxId(args[i]));            
        }
        
        NCBITaxonNode commonAncestor = TaxonomyAlgo.lowestCommonAncestor(nodes);
        
        System.out.println("Common ancestor: " + commonAncestor.getScientificName() + " , " + commonAncestor.getTaxId());
        
        bio4jManager.shutDown();
        
    }
    
}
