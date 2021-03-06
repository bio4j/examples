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
package com.ohnosequences.bio4j.tools.algo;


import com.ohnosequences.bio4j.neo4j.model.nodes.ProteinNode;
import com.ohnosequences.bio4j.neo4j.model.util.Bio4jManager;
import com.ohnosequences.bio4j.neo4j.model.util.NodeRetriever;
import com.ohnosequences.util.Executable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PropertyContainer;


/**
 * This program finds interaction paths between two proteins with a set of customizable parameters
 * @author Pablo Pareja Tobes <ppareja@era7.com>
 */
public class FindInteractionPaths implements Executable {

    @Override
    public void execute(ArrayList<String> array) {
        String[] args = new String[array.size()];
        for (int i = 0; i < array.size(); i++) {
            args[i] = array.get(i);
        }
        main(args);
    }

    public static void main(String args[]) {

        if (args.length != 6) {
            System.out.println("This program expects the following parameters:\n"
                    + "1. Source protein accession \n"
                    + "2. Target protein accession \n"
                    + "3. Max depth \n"
                    + "4. Bio4j DB folder \n"
                    + "5. Max number of results \n"
                    + "6. Results out file name (txt)");
        } else {
            BufferedWriter outBuff = null;

            try {

                Bio4jManager manager = new Bio4jManager(args[3]);
                NodeRetriever nodeRetriever = new NodeRetriever(manager);

                int maxDepth = Integer.parseInt(args[2]);
                int maxResults = Integer.parseInt(args[4]);

                String prot1St = args[0];
                String prot2St = args[1];

                ProteinNode sourceProt = nodeRetriever.getProteinNodeByAccession(prot1St);
                ProteinNode targetProt = nodeRetriever.getProteinNodeByAccession(prot2St);

                System.out.println("Looking for shortest paths from " + prot1St + " to " + prot2St + " ...");
                List<List<String>> results = InteractionsPathFinder.findShortestInteractionPath(sourceProt,
                        targetProt,
                        maxDepth,
                        maxResults);

                System.out.println("donee!");

                for (List<String> list : results) {
                    System.out.println("Path found: ");
                    String tempSt = "";
                    for (String string : list) {
                        tempSt += string + " --> ";
                    }
                    System.out.println(tempSt.substring(0, tempSt.length() - 5));
                }

                System.out.println("Looking now for all simple paths between these two nodes...");

                Iterator<Path> pathsIterator = InteractionsPathFinder.getAllSimpleInteractionPathsBetweenProteins(sourceProt,
                        targetProt,
                        maxDepth);

                outBuff = new BufferedWriter(new FileWriter(new File(args[5])));
                
                for (int i = 0; i < maxResults; i++) {
                    if (pathsIterator.hasNext()) {                        
                        String lineSt = "";
                        
                        Path currentPath = pathsIterator.next();       
                        Iterator<PropertyContainer> pathIterator = currentPath.iterator();
                        boolean isNode = true;
                        while (pathIterator.hasNext()) {
                            
                            if (isNode) {
                                lineSt += (new ProteinNode((Node) pathIterator.next()).getAccession() + ",");
                            } else {
                                pathIterator.next();
                            }
                            isNode = !isNode;
                        }
                        outBuff.write(lineSt.substring(0,lineSt.length() - 1) + "\n");
                    }
                }

                outBuff.close();

                System.out.println("closing manager...");
                manager.shutDown();

                System.out.println("done!");

            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    outBuff.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        }


    }
}
