package ptv.feed.sdc.sender.spec

import org.custommonkey.xmlunit.XMLUnit
import org.springframework.oxm.Marshaller
import org.springframework.oxm.Unmarshaller
import org.springframework.oxm.jaxb.Jaxb2Marshaller
import spock.lang.Specification

import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


class XmlSpecification extends Specification{

  def setupSpec(){
    XMLUnit.setIgnoreWhitespace(true)
    XMLUnit.setIgnoreComments(true)
    XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true)
    XMLUnit.setNormalizeWhitespace(true)
  }


  def toXML(Object bean, Marshaller marshaller) {
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    marshaller.marshal(bean, result)
    return writer.toString()
  }

  def Object fromXML(String xml, Unmarshaller unmarshaller) {
    InputStream is = new ByteArrayInputStream( xml.getBytes( "UTF-8" ) );
    Object bean = unmarshaller.unmarshal(new StreamSource(is))
    return bean;
  }

  def getUnmarshallerFor(clazz) {
    Marshaller marshaller = new Jaxb2Marshaller()
    marshaller.classesToBeBound = [clazz]
    return marshaller
  }
}

