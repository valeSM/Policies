#test paper's licenses
java -cp $CLASSPATH:pl.jar Fus policies/License5paper.ttl policies/License5paper_comp.ttl > policies/outs/pol.ttl
java -cp $CLASSPATH:pl.jar Apply policies/outs/pol.ttl pl/pl-rulesF-1.jena > policies/outs/pol3.ttl
java -cp $CLASSPATH:pl.jar Apply policies/outs/pol3.ttl pl/pl-rulesF-2.jena > policies/outs/pol4.ttl
java -cp $CLASSPATH:pl.jar Apply policies/outs/pol4.ttl pl/pl-rulesF-3.jena > policies/outs/pol5.ttl
java -cp $CLASSPATH:pl.jar Cons policies/outs/pol5.ttl > policies/outs/pol6.ttl

