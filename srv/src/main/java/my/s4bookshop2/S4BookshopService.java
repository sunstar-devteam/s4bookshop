package my.s4bookshop2;

import java.util.*;

import com.sap.cloud.sdk.service.prov.api.annotations.*;
import com.sap.cloud.sdk.service.prov.api.*;
import com.sap.cloud.sdk.service.prov.api.operations.Query;
import com.sap.cloud.sdk.service.prov.api.request.*;
import com.sap.cloud.sdk.service.prov.api.response.*;
import com.sap.cloud.sdk.odatav2.connectivity.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.sdk.s4hana.connectivity.*;
import com.sap.cloud.sdk.s4hana.datamodel.odata.namespaces.businesspartner.Supplier;
import com.sap.cloud.sdk.s4hana.datamodel.odata.services.DefaultBusinessPartnerService;


public class S4BookshopService {

  Logger logger = LoggerFactory.getLogger(S4BookshopService.class);

	private static final String DESTINATION_NAME = "SunstarQA"; //Refers to the destination created in Step 6
	// private static final String apikey = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"; //Replace with your API key


	private ErpConfigContext context = new ErpConfigContext(DESTINATION_NAME);

  @Query(serviceName = "S4BookshopService", entity = "Suppliers")
	public QueryResponse querySupplier(QueryRequest qryRequest) {

    QueryResponse queryResponse;
    int top = qryRequest.getTopOptionValue();
    int skip = qryRequest.getSkipOptionValue();

    try {
    	// Create Map containing request header information
        	Map<String, String> requestHeaders = new HashMap<>();
        	// requestHeaders.put("Content-Type","application/json");
        	// requestHeaders.put("APIKey",apikey);

    	final List<Supplier> suppliers =
    	             new DefaultBusinessPartnerService().getAllSupplier()
    	            .withCustomHttpHeaders(requestHeaders).onRequestAndImplicitRequests()
    	            .select(Supplier.SUPPLIER, Supplier.SUPPLIER_NAME)
    	            .top(top >= 0 ? top : 50)
    	            .skip(skip >= 0 ? skip : -1)
    	           .execute(context);
    	queryResponse = QueryResponse.setSuccess().setData(suppliers).response();

    } catch (final ODataException e) {
    	logger.error("Error occurred with the Query operation: " + e.getMessage(), e);
    	ErrorResponse er = ErrorResponse.getBuilder()
    	                            .setMessage("Error occurred with the Query operation: " + e.getMessage())
    	                            .setStatusCode(500).setCause(e).response();
    	queryResponse = QueryResponse.setError(er);
    }

	  return queryResponse;
	}

  @Function(serviceName = "S4BookshopService", Name = "GetSupplier")
	public OperationResponse getSupplierOfOrder(OperationRequest functionRequest, ExtensionHelper extensionHelper) {
		OperationResponse opResponse;

		try {
			// Retrieve the parameters of the function from the
			// OperationRequest object
			Map<String, Object> parameters = functionRequest.getParameters();

			//Get the DataSourceHandler object from the ExtensionHelper. This is required
			//to execute operations on the local HANA database
			DataSourceHandler handler = extensionHelper.getHandler();

			//Retrieve the Order ID from the request
			Map<String, Object> ordersKey = new HashMap<String, Object>();
			ordersKey.put("ID", String.valueOf(parameters.get("OrderID")));

			List<String> orderElements = Arrays.asList("ID","SUPPLIER");

			//Read the Order information from the local HANA database
			EntityData orderData = handler.executeRead("Orders", ordersKey, orderElements);

			String supplierID = "";
			List<Object> supplierList = new ArrayList<Object>();
			if (orderData != null) {
				//Retrieve the supplier ID from the order information
				supplierID = orderData.getElementValue("SUPPLIER").toString();

				// Create Map containing request header information required for querying the S/4HANA system
	        	Map<String, String> requestHeaders = new HashMap<>();
	        	// requestHeaders.put("Content-Type","application/json");
	        	// requestHeaders.put("APIKey",apikey);

	        	//Use SAP S/4HANA Cloud SDK's Virtual Data Model to read the Supplier information
				final Supplier supplier =
				             new DefaultBusinessPartnerService().getSupplierByKey(supplierID)
				            .withCustomHttpHeaders(requestHeaders).onRequestAndImplicitRequests()
				            .select(Supplier.SUPPLIER, Supplier.SUPPLIER_NAME)
				           .execute(context);

				// Add the retrieved entity data to a supplier list object
				supplierList.add(supplier);
			}

			// Return an instance of OperationResponse containing the list of supplier data
			opResponse = OperationResponse.setSuccess().setData(supplierList).response();

		} catch (Exception e) {
			logger.error("Error in GetSupplier: " + e.getMessage());
			// Return an instance of OperationResponse containing the error in
			// case of failure
			ErrorResponse errorResponse = ErrorResponse.getBuilder()
					.setMessage(e.getMessage())
					.setCause(e)
					.response();

			opResponse = OperationResponse.setError(errorResponse);
		}
		return opResponse;
	}

}