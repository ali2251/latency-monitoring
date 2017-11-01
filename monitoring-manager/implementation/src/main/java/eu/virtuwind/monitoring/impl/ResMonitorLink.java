package eu.virtuwind.monitoring.impl;


import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;

/**
 * Created by Ali on 15/05/2017.
 */
public class ResMonitorLink {

    private String linkId;
    // currentSpeed was replaced with bandwidth
    private Long bandwidth;
    private Long packetLoss;
    private Long packetDelay;
    private Long jitter;
    private Long throughput;
    private Link link;

    public ResMonitorLink(Long bandwidth, Long packetLoss, Long packetDelay, Long jitter, Long  throughput, Link link) {
        this.linkId = link.getLinkId().getValue();;
        this.bandwidth = bandwidth;
        this.packetLoss = packetLoss;
        this.packetDelay = packetDelay;
        this.jitter = jitter;
        this.throughput = throughput;
        this.link = link;
    }

    public Long getThroughput() {
        return throughput;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Long getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(Long packetLoss) {
        this.packetLoss = packetLoss;
    }

    public Long getPacketDelay() {
        return packetDelay;
    }

    public void setPacketDelay(Long packetDelay) {
        this.packetDelay = packetDelay;
    }

    public Long getJitter() {
        return jitter;
    }

    public void setJitter(Long jitter) {
        this.jitter = jitter;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Link getLink() {
        return link;
    }


    @Override
    public String toString() {
        return "ResMonitorLink{" +
                "linkId='" + linkId + '\'' +
                ", bandwidth=" + bandwidth +
                ", packetLoss=" + packetLoss +
                ", packetDelay=" + packetDelay +
                ", jitter=" + jitter +
                '}';
    }
}
