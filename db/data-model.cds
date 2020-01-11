// namespace my.bookshop;
// using { User, Country, managed } from '@sap/cds/common';

// entity Books {
//   key ID : Integer;
//   title  : localized String;
//   author : Association to Authors;
//   stock  : Integer;
// }

// entity Authors {
//   key ID : Integer;
//   name   : String;
//   books  : Association to many Books on books.author = $self;
// }

// entity Orders : managed {
//   key ID  : UUID;
//   book    : Association to Books;
//   country : Country;
//   amount  : Integer;
// }
namespace my.s4bookshop;

entity Books {
  key ID : Integer;
  title  : String;
  author : Association to Authors;
  stock  : Integer;
}

entity Authors {
  key ID : Integer;
  name   : String;
  books  : Association to many Books on books.author = $self;
}

entity Orders {
  key ID : UUID;
  book   : Association to Books;
  buyer  : String;
  date   : DateTime;
  amount : Integer;
  supplier : Integer;
}