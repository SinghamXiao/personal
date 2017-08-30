
XML eXtensible Markup Language

XML Schema（）

	XML Schema 语言也称作 XML Schema 定义（XML Schema Definition，XSD）学习 ，XML Schema的作用是约定xml的标签和类型
		XML Schema描述了XML文档的结构。可以用一个指定的XML Schema来验证某个XML文档，以检查该XML文档是否符合其要求。文档设计者可以通过XML Schema指定一个XML文档所允许的结构和内容，并可据此检查一个XML文档是否是有效的。XML Schema本身是一个XML文档，它符合XML语法结构。可以用通用的XML解析器解析它。
	    一个XML Schema会定义：文档中出现的元素、文档中出现的属性、子元素、子元素的数量、子元素的顺序、元素是否为空、元素和属性的数据类型、元素或属性的默认和固定值。


SOAP（Simple Object Access Protocol, 简单对象访问协议）

	一条 SOAP 消息就是一个普通的 XML 文档，包含下列元素：

        必需的 Envelope 元素，可把此 XML 文档标识为一条 SOAP 消息
        
        可选的 Header 元素，包含头部信息
        
        必需的 Body 元素，包含所有的调用和响应信息
        
        可选的 Fault 元素，提供有关在处理此消息所发生错误的信息

        所有以上的元素均被声明于针对 SOAP 封装的默认命名空间中：

        http://www.w3.org/2001/12/soap-envelope

        以及针对 SOAP 编码和数据类型的默认命名空间：

        http://www.w3.org/2001/12/soap-encoding

	重要的语法规则：

        SOAP 消息必须用 XML 来编码
        
        SOAP 消息必须使用 SOAP Envelope 命名空间
        
        SOAP 消息必须使用 SOAP Encoding 命名空间
        
        SOAP 消息不能包含 DTD 引用
        
        SOAP 消息不能包含 XML 处理指令
	
		<?xml version="1.0" encoding="utf-8"?>

        <soap:Envelope xmlns:soap="http://www.w3.org/2001/12/soap-envelope"
         soap:encodingStyle="http://www.w3.org/2001/12/soap-encoding">  
          <soap:Header></soap:Header>  
          <soap:Body> 
            <soap:Fault></soap:Fault> 
          </soap:Body> 
        </soap:Envelope>
        
HTTP(S) Hypertext Transfer Protocol Secure，缩写：HTTPS，常称为HTTP over TLS，HTTP over SSL或HTTP Secure