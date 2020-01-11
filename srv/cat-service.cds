// using { my.bookshop, sap.common } from '../db/data-model';

// service CatalogService {
//   entity Books @readonly as projection on bookshop.Books;
//   entity Authors @readonly as projection on bookshop.Authors;
//   entity Orders @insertonly as projection on bookshop.Orders;
// }
using my.s4bookshop from '../db/data-model';
// using API_BUSINESS_PARTNER as bp from '../srv/external/csn/ODataServiceforBusinessPartner';
using API_BUSINESS_PARTNER as bp from '../srv/external/csn/BusinessPartnerA2X';

service S4BookshopService {
  entity Books as projection on s4bookshop.Books;
  entity Authors @readonly as projection on s4bookshop.Authors;
  entity Orders @insertonly as projection on s4bookshop.Orders;
  entity Suppliers as projection on bp.A_SupplierType
	   excluding {to_SupplierCompany, to_SupplierPurchasingOrg};

  function GetSupplier(OrderID:UUID) returns Suppliers;
}