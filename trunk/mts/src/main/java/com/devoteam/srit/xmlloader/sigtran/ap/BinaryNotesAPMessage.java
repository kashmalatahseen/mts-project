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

package com.devoteam.srit.xmlloader.sigtran.ap;

import gp.utils.arrays.Array;
import gp.utils.arrays.DefaultArray;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;
import org.dom4j.Element;

import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.Component;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.ISDN_AddressString;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.Invoke;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.InvokeIdType;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.InvokeParameter;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.Mo_forwardSM_Arg;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.Operation;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.OperationLocalvalue;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.ServiceCentreAddress;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.Sm_RP_DA;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.Sm_RP_OA;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.map.Sm_RP_UI;
import com.devoteam.srit.xmlloader.sigtran.ap.generated.tcap.TCMessage;


/**
 *
 * @author fhenry
 */
public class BinaryNotesAPMessage extends APMessage 
{

	// MAP bionarynotes object
	Component mapComponent; 
	
    public BinaryNotesAPMessage() 
    {
    	/*
    	// define MAP messages (MAP.asn file)
    	this.mapComponent = new Component();

    	Invoke invoke = new Invoke();
    	
    	Operation op = new Operation();
    	OperationLocalvalue opLocal = new OperationLocalvalue();
    	opLocal.setValue(46L);
    	op.selectLocalValue(opLocal);
    	invoke.setOpCode(op);
    	
    	InvokeIdType invType = new InvokeIdType(); 
    	invType.setValue(1);
    	invoke.setInvokeID(invType);
    	
    	InvokeParameter invokeParameter = new InvokeParameter();
    	invoke.setInvokeparameter(invokeParameter);
    	Mo_forwardSM_Arg moforwardSM_Arg = new Mo_forwardSM_Arg();
    	Sm_RP_DA sm_rp_da = new Sm_RP_DA();
    	ServiceCentreAddress serviceCentreAddress = new ServiceCentreAddress();
    	serviceCentreAddress.setValue(new byte[]{(byte)0x91,(byte)0x33,(byte)0x66,(byte)0x60,(byte)0x05,(byte)0x67,(byte)0xf9});
    	sm_rp_da.selectServiceCentreAddressDA(serviceCentreAddress);
    	moforwardSM_Arg.setSm_RP_DA(sm_rp_da);
    	
    	Sm_RP_OA sm_rp_oa = new Sm_RP_OA();
    	ISDN_AddressString isdn_AddressString = new ISDN_AddressString();
    	isdn_AddressString.setValue(new byte[]{(byte)0x91,(byte)0x33,(byte)0x66,(byte)0x31,(byte)0x70,(byte)0x71,(byte)0xf3});
    	sm_rp_oa.selectMsisdn(isdn_AddressString);
    	moforwardSM_Arg.setSm_RP_OA(sm_rp_oa);
    	
    	Sm_RP_UI sm_rp_ui = new Sm_RP_UI();
    	sm_rp_ui.setValue(new byte[]{(byte)0x11,(byte)0x08,(byte)0x0b,(byte)0x91,(byte)0x33,(byte)0x66,(byte)0x60,(byte)0x05,(byte)0x67,(byte)0xf7,(byte)0x00,(byte)0x00,(byte)0xa9,(byte)0x06,(byte)0xf3,(byte)0xf9,(byte)0x7c,(byte)0x3e,(byte)0x9f,(byte)0x03});
    	moforwardSM_Arg.setSm_RP_UI(sm_rp_ui);
    	
    	invokeParameter.setValue(moforwardSM_Arg);
    	this.mapComponent.selectInvoke(invoke);
    	*/
    }

    public Array encode() throws Exception 
    {
    	// Library binarynotes
    	IEncoder<com.devoteam.srit.xmlloader.sigtran.ap.generated.map.Component> encoderMAP = CoderFactory.getInstance().newEncoder("BER");
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        encoderMAP.encode(this.mapComponent, outputStream);
        byte[] bytesMAP = outputStream.toByteArray();
        Array arrayMAP = new DefaultArray(bytesMAP);
        // String strMAP = Utils.toHexaString(bytesMAP, null);
		
        return arrayMAP;
    }
          
    
    public void decode(Array array) throws Exception 
    {
        
    	IDecoder decoder = CoderFactory.getInstance().newDecoder("BER");
        InputStream inputStream = new ByteArrayInputStream(array.getBytes());
        this.mapComponent = decoder.decode(inputStream, Component.class);
        
    }

    public void parseFromXML(Element root) throws Exception 
    {
        List<Element> children = root.elements();
        //for (Element element : children) 
        {
            String packageName = "com.devoteam.srit.xmlloader.sigtran.ap.generated.map.";
            Class thisClass = Class.forName(packageName + "Component");
            this.mapComponent = (Component) thisClass.newInstance();
            XMLToASNParser.getInstance().initObject(this.mapComponent, root, packageName);
        }
    }

    public String toXML()
    {
        String ret = "";
        ret += "<AP>";
        ret += ASNToXMLConverter.getInstance().toXML(null,this.mapComponent, 0);
        ret += "\n";
        ret += "</AP>";
    	return ret;
    }
    
}