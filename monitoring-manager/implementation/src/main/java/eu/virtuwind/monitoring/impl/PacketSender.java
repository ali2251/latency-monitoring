package eu.virtuwind.monitoring.impl;


import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetQueueActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.queue.action._case.SetQueueActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnectorKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.TransmitPacketInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.TransmitPacketInputBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.Future;


public class PacketSender {

    private PacketProcessingService packetProcessingService;
    private Logger LOG = LoggerFactory.getLogger(PacketSender.class);

    public static Long sentTime = 0L;


    public PacketSender(PacketProcessingService packetProcessingService){
        this.packetProcessingService = packetProcessingService;

    }

    public boolean sendPacket(long queue, String outputNodeConnector, String nodeId) {



        MacAddress srcMacAddress = new MacAddress("BA:DB:AD:BA:DB:AD");


        String NODE_ID = nodeId;
        String nodeConnectorId = outputNodeConnector.split(":")[2];



        NodeRef ref = createNodeRef(NODE_ID);

        NodeConnectorId ncId = new NodeConnectorId(outputNodeConnector);
        NodeConnectorKey nodeConnectorKey = new NodeConnectorKey(ncId);
        NodeConnectorRef nEgressConfRef = new NodeConnectorRef(createNodeConnRef(NODE_ID, nodeConnectorKey));




        byte[] lldpFrame = LLDPUtil.buildLldpFrame(new NodeId(nodeId),
                new NodeConnectorId(outputNodeConnector), srcMacAddress, Long.parseLong(nodeConnectorId) );


            ActionBuilder actionBuilder = new ActionBuilder();

            Action queueAction = actionBuilder
                    .setOrder(0).setAction(new SetQueueActionCaseBuilder()
                            .setSetQueueAction(new SetQueueActionBuilder()
                                    .setQueueId(queue)
                                    .build())
                            .build())
                    .build();


            Action outputNodeConnectorAction = actionBuilder
                    .setOrder(1).setAction(new OutputActionCaseBuilder()
                            .setOutputAction(new OutputActionBuilder()
                                    .setOutputNodeConnector(new Uri(nodeConnectorId))
                                    .build())
                            .build())
                    .build();

         /*   OutputActionBuilder output = new OutputActionBuilder();
            output.setMaxLength(OFConstants.OFPCML_NO_BUFFER);
            Uri value = new Uri(OutputPortValues.CONTROLLER.toString());
            output.setOutputNodeConnector(value);
            actionBuilder.setAction(new OutputActionCaseBuilder().setOutputAction(output.build()).build());
            actionBuilder.setOrder(2);*/


            ArrayList<Action> actions = new ArrayList<>();
            actions.add(queueAction);
            actions.add(outputNodeConnectorAction);
           // actions.add(actionBuilder.build());



            TransmitPacketInput packet = new TransmitPacketInputBuilder()
                    .setEgress(nEgressConfRef)
                    .setNode(ref)
                    .setPayload(lldpFrame)
                    .setAction(actions)
                    .build();



            Future<RpcResult<Void>> future = packetProcessingService.transmitPacket(packet);
            try {
                if (future.get().isSuccessful()) {
                    sentTime = System.nanoTime();

                    return true;
                } else {
                    System.out.println("failed and error is " + future.get().getErrors().toString());
                    return false;
                }


            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }



    }




    private NodeConnectorRef createNodeConnRef(String node_id, NodeConnectorKey nodeConnKey) {
        InstanceIdentifier<NodeConnector> instanceIdent= InstanceIdentifier.builder(Nodes.class)
                .child(Node.class, new NodeKey(NodeId.getDefaultInstance(node_id)))
                .child(NodeConnector.class, new NodeConnectorKey(nodeConnKey)).toInstance();
        return new NodeConnectorRef(instanceIdent);
    }

    private NodeRef createNodeRef(String node_id) {
        NodeKey key = new NodeKey(new NodeId(node_id));
        InstanceIdentifier<Node> path = InstanceIdentifier.builder(Nodes.class).child(Node.class, key).build();
        return new NodeRef(path);
    }

}
