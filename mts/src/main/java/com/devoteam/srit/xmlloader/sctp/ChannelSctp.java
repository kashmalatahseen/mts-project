/* 
 * Copyright 2012 Devoteam http://www.devoteam.com
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 * 
 * 
 * This file is part of Multi-Protocol Test Suite (MTS).
 * 
 * Multi-Protocol Test Suite (MTS) is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License.
 * 
 * Multi-Protocol Test Suite (MTS) is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Multi-Protocol Test Suite (MTS).
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package com.devoteam.srit.xmlloader.sctp;

import com.devoteam.srit.xmlloader.core.Runner;
import com.devoteam.srit.xmlloader.core.exception.ExecutionException;
import com.devoteam.srit.xmlloader.core.log.GlobalLogger;
import com.devoteam.srit.xmlloader.core.log.TextEvent.Topic;
import com.devoteam.srit.xmlloader.core.newstats.StatPool;
import com.devoteam.srit.xmlloader.core.protocol.Channel;
import com.devoteam.srit.xmlloader.core.protocol.Listenpoint;
import com.devoteam.srit.xmlloader.core.protocol.Msg;
import com.devoteam.srit.xmlloader.core.protocol.Stack;
import com.devoteam.srit.xmlloader.core.protocol.StackFactory;
import com.devoteam.srit.xmlloader.core.utils.Config;
import com.devoteam.srit.xmlloader.core.utils.Utils;
import com.devoteam.srit.xmlloader.tcp.bio.ChannelTcpBIO;
import com.devoteam.srit.xmlloader.tcp.nio.ChannelTcpNIO;

import dk.i1.sctp.OneToOneSCTPSocket;
import dk.i1.sctp.sctp_initmsg;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Collection;

import dk.i1.sctp.AssociationId;

import java.net.Socket;

import org.dom4j.Element;

/**
 *
 * @author nghezzaz
 */

//  channel is called association in SCTP 
public class ChannelSctp extends Channel
{
    private SocketSctp socket;
    
    private Listenpoint listenpoint;

    private AssociationId aid;
    private Collection<InetAddress> localHostList;
    private Collection<InetAddress> remoteHostList;
    private sctp_initmsg initmsg;

    private long startTimestamp = 0;    
    
    /** Creates a new instance of Channel*/
    public ChannelSctp(Stack stack)
    {
    	super(stack);
    }

    public ChannelSctp(ListenpointSctp aListenpoint, String aLocalHost, int aLocalPort, String aRemoteHost, int aRemotePort, String aProtocol) throws Exception
    {
        super(aLocalHost, aLocalPort, aRemoteHost, aRemotePort, aProtocol);
        this.socket = null;
        this.listenpoint = aListenpoint;
        this.initmsg = new sctp_initmsg();
    }

    public ChannelSctp(String name, Listenpoint aListenpoint, Socket aSocket) throws Exception
    {
		super(
        		name,
        		Utils.getLocalAddress().getHostAddress(),
        		Integer.toString(((OneToOneSCTPSocket)aSocket).getLocalInetPort()),
        		null,
        		null,
        		aListenpoint.getProtocol()
        );
		
		StatPool.beginStatisticProtocol(StatPool.CHANNEL_KEY, StatPool.BIO_KEY, StackFactory.PROTOCOL_SCTP, getProtocol());
		this.startTimestamp = System.currentTimeMillis();

        this.listenpoint = aListenpoint;
        this.socket = new SocketSctp((OneToOneSCTPSocket) aSocket);
        this.socket.setChannelSctp(this);
        this.initmsg = new sctp_initmsg();
    }
    
    public SocketSctp getSocketSctp()
    {
        return socket;
    }

    public AssociationId getAssociationId()
    {
        return aid;
    }

    public void setAssociationId(AssociationId aid)
    {
        this.aid = aid;
    }

    /** Open a Channel */
    @Override
    public boolean open() throws Exception
    {
        if (socket == null)
        {
    		StatPool.beginStatisticProtocol(StatPool.CHANNEL_KEY, StatPool.BIO_KEY, StackFactory.PROTOCOL_SCTP, getProtocol());
    		this.startTimestamp = System.currentTimeMillis();
        	
			//InetAddress localAddr = InetAddress.getByName(getLocalHost());
			// TODO Take localAddr into account
            OneToOneSCTPSocket sctpSocket = new OneToOneSCTPSocket();
            sctpSocket.bind(getLocalPort()); 
            sctpSocket.setInitMsg(this.initmsg);
    		            
            InetSocketAddress remoteSocketAddress = new InetSocketAddress(getRemoteHost(), getRemotePort());
            sctpSocket.connect(remoteSocketAddress);
        
            this.localPort = sctpSocket.getLocalInetPort();
            // TODO Take socket LocalAddress into account
            // this.setLocalHost(socket.getLocalAddress().getHostAddress());

            socket = new SocketSctp(sctpSocket);
        }
        
        socket.setChannelSctp(this);
        socket.setDaemon(true);
        socket.start();        

        return true;
    }

    /** Close a Channel */
    @Override
    public boolean close()
    {	
    	if (socket != null)
    	{
    		StatPool.endStatisticProtocol(StatPool.CHANNEL_KEY, StatPool.BIO_KEY, StackFactory.PROTOCOL_SCTP, getProtocol(), startTimestamp);
    		
    		socket.shutdown();
	        socket = null;
    	}
        return true;
    }

    
    /** Send a Msg to Channel */
    @Override
    public synchronized boolean sendMessage(Msg msg) throws Exception
    {
        if (socket == null)
        {
            throw new ExecutionException("SocketSctp is null, has the connection been opened ?");
        }
        msg.setChannel(this);
        socket.send(msg);
        return true;
    }
    
    /** Get the transport protocol */
    @Override
    public String getTransport() 
    {
    	return StackFactory.PROTOCOL_SCTP;
    }
    
	public Listenpoint getListenpointSctp() {
		return listenpoint;
	}

	
    //---------------------------------------------------------------------
    // methods for the XML display / parsing
    //---------------------------------------------------------------------
	
    /** 
     * Parse the message from XML element 
     */
    @Override
    public void parseFromXml(Element root, Runner runner, String protocol) throws Exception
    {
    	super.parseFromXml(root, runner, protocol);
    	
    	this.initmsg = new sctp_initmsg();
		String num_ostreams = root.attributeValue("num_ostreams");
		if (num_ostreams == null)
		{
    		num_ostreams = this.stack.getConfig().getString("connect.NUM_OSTREAMS");
		}
		if (num_ostreams != null)
		{
			this.initmsg.sinit_num_ostreams = (short) Integer.parseInt(num_ostreams);
    		GlobalLogger.instance().getApplicationLogger().debug(Topic.PROTOCOL, "initmsg.sinit_num_ostreams=", this.initmsg.sinit_num_ostreams);
		}

		String max_instreams = root.attributeValue("max_instreams");
		if (max_instreams == null)
		{
			max_instreams = this.stack.getConfig().getString("connect.MAX_INSTREAMS");
		}
    	if (max_instreams != null)
    	{
    		this.initmsg.sinit_max_instreams = (short) Integer.parseInt(max_instreams);
    		GlobalLogger.instance().getApplicationLogger().debug(Topic.PROTOCOL, "initmsg.sinit_max_instreams=", this.initmsg.sinit_max_instreams);
    	}
		
    	String max_attempts = root.attributeValue("max_attempts");
		if (max_attempts == null)
		{
			max_attempts = this.stack.getConfig().getString("connect.MAX_ATTEMPTS");
		}
		if (max_attempts != null)
		{
			this.initmsg.sinit_max_attempts = (short) Integer.parseInt(max_attempts);
			GlobalLogger.instance().getApplicationLogger().debug(Topic.PROTOCOL, "initmsg.sinit_max_attempts=", initmsg.sinit_max_attempts);    			
		}
		
		String max_init_timeo = root.attributeValue("max_initTimeo");
		if (max_init_timeo == null)
		{
			max_init_timeo = this.stack.getConfig().getString("connect.MAX_INIT_TIMEO");
		}
		if (max_init_timeo != null)
		{
			this.initmsg.sinit_max_init_timeo= (short) Integer.parseInt(max_init_timeo);
			GlobalLogger.instance().getApplicationLogger().debug(Topic.PROTOCOL, "initmsg.sinit_max_init_timeo=", initmsg.sinit_max_init_timeo);    			
		}
    }

}



