package eu.virtuwind.monitoring.impl;


import com.google.common.collect.ImmutableList;
import org.opendaylight.openflowplugin.api.OFConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;

import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetQueueActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.queue.action._case.SetQueueActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.OutputPortValues;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnectorKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.basepacket.rev140528.packet.chain.grp.packet.chain.Packet;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.basepacket.rev140528.packet.chain.grp.packet.chain.packet.RawPacketBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.RawPacket;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.TransmitPacketInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.TransmitPacketInputBuilder;
import org.opendaylight.yangtools.yang.binding.DataContainer;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.opendaylight.yang.gen.v1.urn.opendaylight.packet.ethernet.rev140528.KnownEtherType.Arp;


public class PacketSender {

    private  PacketProcessingService packetProcessingService;
    private Logger LOG = LoggerFactory.getLogger(PacketSender.class);

    public static Long sentTime = 0L;


    public PacketSender(PacketProcessingService packetProcessingService){
        this.packetProcessingService = packetProcessingService;
    }

    public void sendPacket(MacAddress srcMacAddress1, MacAddress destMacAddress) {




        MacAddress srcMacAddress = new MacAddress("00:00:00:00:00:09");


        String NODE_ID = "openflow:1";
        Long bufferId = null;

        NodeRef ref = createNodeRef(NODE_ID);
        NodeConnectorId ncId = new NodeConnectorId("openflow:1:2");
        NodeConnectorKey nodeConnectorKey = new NodeConnectorKey(ncId);
        NodeConnectorRef nEgressConfRef = new NodeConnectorRef(createNodeConnRef(NODE_ID, nodeConnectorKey));




        byte[] lldpFrame = LLDPUtil.buildLldpFrame(new NodeId("openflow:1"),
                new NodeConnectorId("openflow:1:2"), srcMacAddress, 2L);


            ActionBuilder actionBuilder = new ActionBuilder();

            Action queueAction = actionBuilder
                    .setOrder(0).setAction(new SetQueueActionCaseBuilder()
                            .setSetQueueAction(new SetQueueActionBuilder()
                                    .setQueueId((long)1)
                                    .build())
                            .build())
                    .build();


            Action outputNodeConnectorAction = actionBuilder
                    .setOrder(1).setAction(new OutputActionCaseBuilder()
                            .setOutputAction(new OutputActionBuilder()
                                    .setOutputNodeConnector(new Uri("2"))
                                    .build())
                            .build())
                    .build();

            OutputActionBuilder output = new OutputActionBuilder();
            output.setMaxLength(OFConstants.OFPCML_NO_BUFFER);
            Uri value = new Uri(OutputPortValues.CONTROLLER.toString());
            output.setOutputNodeConnector(value);
            actionBuilder.setAction(new OutputActionCaseBuilder().setOutputAction(output.build()).build());
            actionBuilder.setOrder(2);


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
                sentTime = System.currentTimeMillis();
                System.out.println("sent time is: " + sentTime);
                System.out.println( future.get().isSuccessful());
                System.out.println("Is Successful");

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
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