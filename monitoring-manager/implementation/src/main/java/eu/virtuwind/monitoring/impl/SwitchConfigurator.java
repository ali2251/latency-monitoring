package eu.virtuwind.monitoring.impl;

import org.opendaylight.openflowplugin.api.OFConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv6Prefix;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv6MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._4.match.TcpMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.openflow.common.types.rev130731.PortNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwitchConfigurator {

    private static final Logger LOG = LoggerFactory.getLogger(SwitchConfigurator.class);

    private SalFlowService salFlowService;
    private String srcIp;
    private String dstIp;
    private String switchId;
    private boolean isIpv4;


    public SwitchConfigurator(SalFlowService salFlowService, boolean isIpv4, String srcIp, String dstIp) {
        this.salFlowService = salFlowService;
        this.isIpv4 = isIpv4;
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        send();

    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }

    public String getSrcIpv6() {
        return srcIp;
    }


    public void setSwitchId(String switchId) {
        this.switchId = getSwitchId();
    }

    public String getSwitchId() {
        return switchId;
    }

    public void setIsIpv4(boolean isIpv4) {
        this.isIpv4 = isIpv4;
    }

    public boolean getIsIpv4() {
        return isIpv4;
    }


    public void send() {

        LOG.info("Sending...");
        Flow flow = createFlow();
        final AddFlowInputBuilder builder = new AddFlowInputBuilder(flow);
        salFlowService.addFlow(builder.build());
        LOG.info("Finished...");
    }


    private Flow createFlow() {

        FlowBuilder flowBuilder = new FlowBuilder().setFlowName("random").setTableId((short)0);


        flowBuilder
                .setFlowName("CuteFLow")
                .setMatch(createMatch())
                .setBufferId(OFConstants.OFP_NO_BUFFER);


        return flowBuilder.build();
    }


    private Match createMatch() {
        MatchBuilder matchBuilder = new MatchBuilder();
        if (isIpv4) {
            matchBuilder.setLayer3Match(new Ipv4MatchBuilder()
                    .setIpv4Destination(new Ipv4Prefix(dstIp))
                    .setIpv4Source(new Ipv4Prefix(srcIp)).build());
        } else {
            matchBuilder.setLayer3Match(new Ipv6MatchBuilder()
                    .setIpv6Destination(new Ipv6Prefix(dstIp))
                    .setIpv6Source(new Ipv6Prefix(srcIp)).build());
        }

        return matchBuilder.build();
    }
}