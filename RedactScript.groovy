//simon.moffatt@forgerock.com
//28Nov 2018 Dynamic Context Redaction
import org.forgerock.http.header.ContentTypeHeader

logger.info("Redaction Script started")
//Pull attributes response from
def contextAdvice = contexts.policyDecision.attributes['Context'][0]

//Iterate over responses
switch (contextAdvice) {
	
	case "allow-redact": 
		logger.info("AM context response was allow-redact")
		break
	case "no-restriction":
		logger.info("AM context response was no-restriction")
		break
}

//return next.handle(context, request)
return next.handle(context, request)
.thenOnResult { response ->
	
	logger.info("Original response from Data API " + response.entity)
	
	if (contextAdvice == "allow-redact") {
		originalEntity = response.entity.getJson()
		originalEntity.cars.each{car->car.price="-----"}
		ContentTypeHeader jsonCTH = new ContentTypeHeader("application/json", "charset=utf-8", "")
		response.putHeaders(jsonCTH)		
		response.setEntity(originalEntity)
		logger.info("Redacted response " + response.entity)		
		
	}		  	
}

