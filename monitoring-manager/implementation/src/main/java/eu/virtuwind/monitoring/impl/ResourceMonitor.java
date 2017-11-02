package eu.virtuwind.monitoring.impl;


import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.address.tracker.rev140617.AddressCapableNodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.address.tracker.rev140617.address.node.connector.Addresses;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNodeConnector;

import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.port.statistics.rev131214.FlowCapableNodeConnectorStatisticsData;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NetworkTopology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TopologyId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyKey;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.opendaylight.yang.gen.v1.urn.eu.virtuwind.monitoring.rev150722.*;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import java.util.concurrent.Future;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

//import org.opendaylight.yang.gen.v1.urn.eu.virtuwind.resourcemonitor.rev161017.*;
//import org.opendaylight.yangtools.yang.binding.DataContainer;

/**
 * Class to provode the static methods to get the Nodes and Links
 * As well as providing a translation from an IP address to a NodeID
 */

public class ResourceMonitor implements MonitoringService {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceMonitor.class);

    private static DataBroker dataBroker;

    private static ResourceMonitor resourceMonitor;

    private static  LatencyMonitor latencyMonitor;

    public static final Integer NUM_OF_QUEUES = 4;
    public static final Long QUEUE_SIZE = 30000L; //in bytes



    public ResourceMonitor(DataBroker db, LatencyMonitor latencyMonitor1) {
        dataBroker = db;
        latencyMonitor = latencyMonitor1;
        resourceMonitor = this;
    }




    public static ResourceMonitor getInstance() {

        return resourceMonitor;
    }

    public Future<RpcResult<GetStatsOutput>> getStats() {

        GetStatsOutputBuilder output = new GetStatsOutputBuilder();
        output.setStats(getAllLinksWithQos().toString());
        return RpcResultBuilder.success(output.build()).buildFuture();
        //setStats

    }

    /**
     * Recieve DataBroker from Path-Manager
     * and return all nodes from Topology.
     * Procedure is as follows:
     * 1. Make a TopologyId with value flow:1
     * 2. Build the Instance Identifier of the Topology
     * 3. Read the transaction from the DataBroker
     * 4. Get the CheckedFuture Of the topology from the Operational Store
     * 5. Return the Nodes if found else empty list
     *
     * @param db - {@link DataBroker} DataBroker to extract data fromn md-sal
     * @return List<Node> {@link List< Node >} - list of nodes found in the topoology
     */


    public static List<Node> getAllNodes(DataBroker db) {
        List<Node> nodeList = new ArrayList<>();

        try {
            //Topology Id
            TopologyId topoId = new TopologyId("flow:1");
            //get the InstanceIdentifier
            InstanceIdentifier<Topology> nodesIid = InstanceIdentifier.builder(NetworkTopology.class).child(Topology.class, new TopologyKey(topoId)).toInstance();
            ReadOnlyTransaction nodesTransaction = db.newReadOnlyTransaction();

            //Read from operational database
            CheckedFuture<Optional<Topology>, ReadFailedException> nodesFuture = nodesTransaction
                    .read(LogicalDatastoreType.OPERATIONAL, nodesIid);
            Optional<Topology> nodesOptional = nodesFuture.checkedGet();

            if (nodesOptional != null && nodesOptional.isPresent()) {
                nodeList = nodesOptional.get().getNode();
            }
            return nodeList;
        } catch (Exception e) {
            LOG.info("Node Fetching Failed");
            return nodeList;
        }

    }

    public void test() {

/*        ResourceMonitor.getAllNodes(dataBroker);
        ResourceMonitor.getAllLinks(dataBroker); */

        ResourceMonitor resourceMonitor = ResourceMonitor.getInstance();

        //resourceMonitor.getAllLinksWithQos();

    }

    public List<ResMonitorLink> getAllLinksWithQos() {

        try {

            List<ResMonitorLink> linksToReturn = new ArrayList<>();

            List<Link> links = getAllLinks(dataBroker);

            List<org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node> nodeList = getNodes(dataBroker);

            if (links != null) {

                LOG.info("Link size is " + links.size());
              //  System.out.println("size of links----->" + links.size());
                for (Link link : links) {
                    String nodeToFind = link.getSource().getSourceNode().getValue();
                    String outputNodeConnector = link.getSource().getSourceTp().getValue();

                    for (org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node node : nodeList) {


                        if (node.getId().getValue().equals(nodeToFind)) {

                            List<NodeConnector> nodeConnectors = node.getNodeConnector();

                            for (NodeConnector nc : nodeConnectors) {

                                if (nc.getId().getValue().equals(outputNodeConnector)) {


                                    FlowCapableNodeConnectorStatisticsData statData = nc
                                            .getAugmentation(FlowCapableNodeConnectorStatisticsData.class);
                                    org.opendaylight.yang.gen.v1.urn.opendaylight.port.statistics.rev131214.flow.capable.node.connector.statistics.FlowCapableNodeConnectorStatistics statistics = statData
                                            .getFlowCapableNodeConnectorStatistics();
                                    BigInteger packetsTransmitted = BigInteger.ZERO;
                                    BigInteger packetErrorsTransmitted = BigInteger.ZERO;
                                    if(statistics != null && statistics.getPackets() != null) {
                                        packetsTransmitted = statistics.getPackets().getTransmitted();
                                        packetErrorsTransmitted = statistics.getTransmitErrors();

                                    }
                                   Float packetLoss = (packetsTransmitted.floatValue() == 0) ? 0
                                            : packetErrorsTransmitted.floatValue()
                                            / packetsTransmitted.floatValue();


                                    //BigInteger totalbytes = statistics.getBytes().getTransmitted();

                                   // Thread.sleep(10000);

                                   // BigInteger totalbytesNow = statistics.getBytes().getTransmitted();



                                    BigInteger throughput = BigInteger.ZERO; //(totalbytesNow.subtract(totalbytes)).divide(new BigInteger("10")) ;

                                    //This is the one
                                    FlowCapableNodeConnector fcnc = nc.getAugmentation(FlowCapableNodeConnector.class);
// for now setting only bandwidth and leaving the other qos empty, but the rest should also be implemented, at least packetDelay!!!
                                    linksToReturn.add(new ResMonitorLink(fcnc.getCurrentSpeed(), packetLoss.longValue(), -1L, -1L, throughput.longValue() ,link));


                                }

                            }

                        }
                    }

                    if(!(link.getLinkId().getValue().contains("host") )) {
                        synchronized (this) {

                            Long latency = latencyMonitor.MeasureNextLink(link);
                            Long jitter = latencyMonitor.MeasureNextLinkJitter(link);
                            linksToReturn.get(linksToReturn.size()-1).setPacketDelay(latency);
                            linksToReturn.get(linksToReturn.size()-1).setJitter(jitter);

                        }
                    }


                }
                return linksToReturn;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * Recieve DataBroker from Path-Manager
     * and return all links fetched from Topology.
     * Procedure is as follows:
     * 1. Make a TopologyId with value flow:1
     * 2. Build the Instance Identifier of the Topology
     * 3. Read the transaction from the DataBroker
     * 4. Get the CheckedFuture Of the topology from the Operational Store
     * 5. Return the Links if found else return empty list
     *
     * @param db - {@link DataBroker} DataBroker to extract data fromn md-sal
     * @return List<Link> - {@link List< Link >} list of links found in the topoology
     */


    public static List<Link> getAllLinks(DataBroker db) {
        List<Link> linkList = new ArrayList<>();

        try {
            TopologyId topoId = new TopologyId("flow:1");
            InstanceIdentifier<Topology> nodesIid = InstanceIdentifier.builder(NetworkTopology.class).child(Topology.class, new TopologyKey(topoId)).toInstance();
            ReadOnlyTransaction nodesTransaction = db.newReadOnlyTransaction();
            CheckedFuture<Optional<Topology>, ReadFailedException> nodesFuture = nodesTransaction
                    .read(LogicalDatastoreType.OPERATIONAL, nodesIid);
            Optional<Topology> nodesOptional = nodesFuture.checkedGet();

            if (nodesOptional != null && nodesOptional.isPresent())
                linkList = nodesOptional.get().getLink();


            // LOG.info("Nodelist: " + nodeList);

            return linkList;
        } catch (Exception e) {

            LOG.info("Node Fetching Failed");

            return linkList;
        }

    }

/*    public List<Link> getAllLinks() {
        List<Link> linkList = new ArrayList<>();

        try {
            TopologyId topoId = new TopologyId("flow:1");
            InstanceIdentifier<Topology> nodesIid = InstanceIdentifier.builder(NetworkTopology.class).child(Topology.class, new TopologyKey(topoId)).toInstance();
            ReadOnlyTransaction nodesTransaction = db.newReadOnlyTransaction();
            CheckedFuture<Optional<Topology>, ReadFailedException> nodesFuture = nodesTransaction
                    .read(LogicalDatastoreType.OPERATIONAL, nodesIid);
            Optional<Topology> nodesOptional = nodesFuture.checkedGet();

            if (nodesOptional != null && nodesOptional.isPresent())
                linkList = nodesOptional.get().getLink();

            // LOG.info("Nodelist: " + nodeList);

            return linkList;
        }

        catch (Exception e) {

            LOG.info("Node Fetching Failed");

            return linkList;
        }

    }*/


    /**
     * Recieves an Ip address of the host or the switch and returns the respective NodeId
     * For Switches NodeID will be of the format openflow:1:2
     * For Hosts NodeID will be of the format host:00:00:00:00:00:01
     *
     * @param ipAddress {@link String} Ip address of the host/switch
     * @param db        {@link DataBroker} Data Broken to retrieve data from
     * @return NodeId  {@link String} NodeId of the host or switch
     */

    public static List<org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node> getNodes(DataBroker db) throws ReadFailedException {
        List<org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node> nodeList = new ArrayList<>();
        InstanceIdentifier<Nodes> nodesIid = InstanceIdentifier.builder(
                Nodes.class).build();
        ReadOnlyTransaction nodesTransaction = db.newReadOnlyTransaction();
        CheckedFuture<Optional<Nodes>, ReadFailedException> nodesFuture = nodesTransaction
                .read(LogicalDatastoreType.OPERATIONAL, nodesIid);
        Optional<Nodes> nodesOptional = Optional.absent();
        nodesOptional = nodesFuture.checkedGet();

        if (nodesOptional != null && nodesOptional.isPresent()) {
            nodeList = nodesOptional.get().getNode();
        }
        return nodeList;
    }


}
