/*
 * Copyright © 2015 George and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package eu.virtuwind.monitoring.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker.ProviderContext;
import org.opendaylight.controller.sal.binding.api.BindingAwareProvider;
import org.opendaylight.controller.sal.binding.api.NotificationProviderService;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.packet.service.rev130709.PacketProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MonitoringProvider implements BindingAwareProvider, AutoCloseable {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoringProvider.class);
    //Members related to MD-SAL operations
    private DataBroker dataBroker;
    private SalFlowService salFlowService;
    private NotificationProviderService notificationService;

    public MonitoringProvider(DataBroker dataBroker, RpcProviderRegistry rpcProviderRegistry,
                              NotificationProviderService notificationService) {
        this.dataBroker = dataBroker;
        this.salFlowService = rpcProviderRegistry.getRpcService(SalFlowService.class);
        this.notificationService = notificationService;

        PacketProcessingService packetProcessingService = rpcProviderRegistry.getRpcService(PacketProcessingService.class);
        System.out.println("Module Loaded Up");
        //SwitchConfigurator flow = new SwitchConfigurator(salFlowService, true, "10.0.0.1", "10.0.0.2");
        PacketProcessing packetProcessing = new PacketProcessing();
        notificationService.registerNotificationListener(packetProcessing);

        final PacketSender packetSender = new PacketSender(packetProcessingService);

        final LatencyMonitor latencyMonitor = new LatencyMonitor(dataBroker, packetProcessingService);

        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("meow");
                LOG.info("Meow");

                latencyMonitor.test();

                System.out.println("done with test");

                //packetSender.sendPacket(new MacAddress("ae:bd:c7:94:15:ef"), new MacAddress("82:fe:07:dc:cb:cb"));
            }
        }, 1, TimeUnit.MINUTES);

    }

    @Override
    public void onSessionInitiated(ProviderContext session) {
        LOG.info("MonitoringProvider Session Initiated");

    }

    @Override
    public void close() throws Exception {
        LOG.info("MonitoringProvider Closed");
    }

}
