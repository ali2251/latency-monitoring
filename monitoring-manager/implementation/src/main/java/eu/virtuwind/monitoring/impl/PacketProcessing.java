package eu.virtuwind.monitoring.impl;


import eu.virtuwind.monitoring.impl.external.PacketParsingUtils;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.*;
import org.opendaylight.yangtools.concepts.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PacketProcessing implements PacketProcessingListener {


    private final Logger LOG = LoggerFactory.getLogger(PacketProcessing.class);
    private final static long FLOOD_PORT_NUMBER = 0xfffffffbL;
    private List<Registration> registrations;
    private DataBroker dataBroker;
    private PacketProcessingService packetProcessingService;
    private List<String> dstMacs;


    public PacketProcessing() {
        LOG.info("PacketProcessing loaded successfully");
        dstMacs = new LinkedList<>();
    }


    @Override
    public void onPacketReceived(PacketReceived packetReceived) {


        byte[] payload = packetReceived.getPayload();

        LOG.info("Packet Received");

        MacAddress srcMacAddress = new MacAddress("00:00:00:00:00:09");


        byte[] lldpFrame = LLDPUtil.buildLldpFrame(new NodeId("openflow:1"),
                new NodeConnectorId("openflow:1:2"), srcMacAddress, 2L);

/*

        byte[] srcMacRaw = PacketParsingUtils.extractSrcMac(payload);
        String srcMac = PacketParsingUtils.rawMacToString(srcMacRaw);
        LOG.info(srcMac);
        byte[] dstMacRaw = PacketParsingUtils.extractDstMac(payload);
        String dstMac = PacketParsingUtils.rawMacToString(dstMacRaw);
        LOG.info(dstMac);

        LOG.info("\n\n\n\n --------------------------------------");

        byte[] srcIpRaw = PacketParsingUtils.extractSrcIP(payload);
        String srcIp = PacketParsingUtils.rawIpToString(srcIpRaw);
        LOG.info("Source IP: " + srcIp);
        byte[] dstIpRaw = PacketParsingUtils.extractDstIP(payload);
        String dstIp = PacketParsingUtils.rawIpToString(dstIpRaw);
*/
//        LOG.info("Destination IP: " + dstIp);

        byte[] srcMacRaw = PacketParsingUtils.extractSrcMac(payload);
        String srcMac = PacketParsingUtils.rawMacToString(srcMacRaw);


        System.out.println(srcMac);


        if(srcMac.equals("00:00:00:00:00:09")) {
            Long timeNow = System.currentTimeMillis();
            Long latency = timeNow - PacketSender.sentTime;

            LatencyMonitor.latency = latency;

          /*  System.out.println("Got the packet    " + System.currentTimeMillis());
            System.out.println("latency is " + (System.currentTimeMillis() - PacketSender.sentTime ) );
*/
            LOG.info("Yaaay, got the packet");
            LOG.debug("YUp");
            LOG.error("Packet is here ");
        }

        LOG.info("src mac -----------"+srcMac);
        byte[] dstMacRaw = PacketParsingUtils.extractDstMac(payload);
        String dstMac = PacketParsingUtils.rawMacToString(dstMacRaw);
        LOG.info("dest mac ------------"+dstMac);
        String protocol;
        byte p = PacketParsingUtils.extractIPprotocol(payload);
        if (p == 0x11)
            protocol = "UDP";
        else
            protocol = "TCP";

        int port = PacketParsingUtils.extractDestPort(payload);

  /*      if (isDestination(dstMac)) {
            forwardPacket(srcMac, dstMac, srcIp, dstIp);
        }
*/

    }


    public void addDestMac(String address) {
        dstMacs.add(address);
    }

    public boolean isDestination(String address) {
        for (String mac : dstMacs) {
            if (mac.equals(address)) {
                return true;
            }
        }
        return false;
    }

    public void forwardPacket(String scrMac, String dstMac, String scrIp, String dstIp) {
        /**
         NodeConnectorRef ingressNodeConnectorRef = packetReceived.getIngress();
         NodeRef ingressNodeRef = InventoryUtils.getNodeRef(ingressNodeConnectorRef);
         NodeConnectorId ingressNodeConnectorId = InventoryUtils.getNodeConnectorId(ingressNodeConnectorRef);
         NodeId ingressNodeId = InventoryUtils.getNodeId(ingressNodeConnectorRef);
         */
        LOG.info("Forwarded packet " + dstMac);
    }

    public void dequeueEnqueueDest(String dstMac) {
        dstMacs.remove(0);
        dstMacs.add(dstMac);
    }
}