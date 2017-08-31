from mininet.net import Mininet
from mininet.node import Controller, RemoteController, OVSController
from mininet.node import CPULimitedHost, Host
from mininet.node import OVSKernelSwitch
from mininet.node import OVSSwitch
from mininet.cli import CLI
from mininet.log import setLogLevel, info
from mininet.link import TCLink, Intf

def MyNetwork():
 print("Starting")
 net = Mininet(controller=Controller, switch=OVSSwitch)
 c1 = RemoteController("c1", ip='127.0.0.1', port=6653)
 #hosts
 h1 = net.addHost('h1', ip='10.0.1.1/24', defaultRoute='via 10.0.1.4', mac='00:00:00:00:01:01')
 h2 = net.addHost('h2', ip='10.0.1.2/24', defaultRoute='via 10.0.1.4', mac='00:00:00:00:01:02')

 #core switches
 s1 = net.addSwitch('s1')
 s2 = net.addSwitch('s2')

 #host links
 net.addLink(s1, h1)
 net.addLink(s1, s2)
 net.addLink(s2, h2)

 #build network
 info("Starting network \n")
 net.build()
 c1.start()
 s1.start([c1])
 s2.start([c1])
 CLI(net)
 net.stop()

if __name__ == '__main__':
    setLogLevel('info')
    MyNetwork()
