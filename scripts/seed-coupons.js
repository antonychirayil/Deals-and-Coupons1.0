// Fills deals_db.coupons with ~1000 realistic test coupons.
//
// Run from the repo root (MongoDB must be running):
//   docker cp scripts/seed-coupons.js deals-mongodb:/tmp/seed-coupons.js
//   docker exec deals-mongodb mongosh -u <MONGO_ROOT_USERNAME> -p <MONGO_ROOT_PASSWORD> --authenticationDatabase admin deals_db --file /tmp/seed-coupons.js
//
// Safe to run again: it first deletes the coupons it created last time (seeded: true)
// and never touches coupons you created yourself.
// Remove all test coupons:  db.coupons.deleteMany({ seeded: true })

const TOTAL = 1000;
const EXPIRED_SHARE = 0.1; // about 10% already expired, to test how the site shows them

const stores = [
  { name: 'Amazon', prefix: 'AMZ', categories: ['Electronics', 'Home', 'Books'] },
  { name: 'Flipkart', prefix: 'FLIP', categories: ['Electronics', 'Fashion', 'Home'] },
  { name: 'Myntra', prefix: 'MYN', categories: ['Fashion', 'Beauty'] },
  { name: 'Ajio', prefix: 'AJIO', categories: ['Fashion'] },
  { name: 'Meesho', prefix: 'MEE', categories: ['Fashion', 'Home'] },
  { name: 'Nykaa', prefix: 'NYK', categories: ['Beauty'] },
  { name: 'Swiggy', prefix: 'SWG', categories: ['Food'] },
  { name: 'Zomato', prefix: 'ZOM', categories: ['Food'] },
  { name: "Domino's", prefix: 'DOM', categories: ['Food'] },
  { name: 'BigBasket', prefix: 'BB', categories: ['Groceries'] },
  { name: 'Blinkit', prefix: 'BLK', categories: ['Groceries'] },
  { name: 'MakeMyTrip', prefix: 'MMT', categories: ['Travel'] },
  { name: 'Goibibo', prefix: 'GOI', categories: ['Travel'] },
  { name: 'Uber', prefix: 'UBER', categories: ['Travel'] },
  { name: 'BookMyShow', prefix: 'BMS', categories: ['Entertainment'] },
  { name: 'Croma', prefix: 'CRO', categories: ['Electronics'] },
  { name: 'Reliance Digital', prefix: 'RD', categories: ['Electronics'] },
  { name: 'boAt', prefix: 'BOAT', categories: ['Electronics'] },
  { name: 'Lenskart', prefix: 'LENS', categories: ['Health', 'Fashion'] },
  { name: 'PharmEasy', prefix: 'PE', categories: ['Health'] },
  { name: 'FirstCry', prefix: 'FC', categories: ['Kids'] },
  { name: 'Pepperfry', prefix: 'PEP', categories: ['Home'] },
  { name: 'Urban Company', prefix: 'UC', categories: ['Services'] },
  { name: 'Puma', prefix: 'PUMA', categories: ['Fashion', 'Sports'] },
  { name: 'Decathlon', prefix: 'DEC', categories: ['Sports'] },
];

const descriptions = [
  (d) => `Flat ${d}% off on your order`,
  (d) => `${d}% off on orders above Rs. ${pick([499, 999, 1499, 1999, 2999])}`,
  (d) => `Extra ${d}% off for new users`,
  (d) => `${d}% off with select bank cards`,
  (d) => `Weekend special: ${d}% off sitewide`,
  (d) => `Up to ${d}% off on bestsellers`,
  (d) => `${d}% cashback on your first purchase`,
  (d) => `Festive offer: ${d}% off, limited time`,
];

const discounts = [5, 10, 10, 15, 15, 20, 20, 25, 30, 40, 50, 60, 70];

function pick(list) {
  return list[Math.floor(Math.random() * list.length)];
}

// The app stores LocalDate as midnight India time (UTC+5:30), so we do the same
function dateDaysFromToday(days) {
  const d = new Date();
  d.setDate(d.getDate() + days);
  const ymd = d.toISOString().slice(0, 10);
  return new Date(`${ymd}T00:00:00+05:30`);
}

const removed = db.coupons.deleteMany({ seeded: true }).deletedCount;

const coupons = [];
for (let i = 0; i < TOTAL; i++) {
  const store = pick(stores);
  const discount = pick(discounts);
  const expired = Math.random() < EXPIRED_SHARE;
  const days = expired ? -(1 + Math.floor(Math.random() * 90)) : 1 + Math.floor(Math.random() * 540);

  coupons.push({
    code: `${store.prefix}${discount}${(i + 1).toString(36).toUpperCase().padStart(3, '0')}`, // unique, e.g. AMZ20A1B
    provider: store.name,
    category: pick(store.categories),
    description: pick(descriptions)(discount),
    discount: discount,
    expiryDate: dateDaysFromToday(days),
    _class: 'com.deals.coupon.entity.Coupon',
    seeded: true,
  });
}

const inserted = db.coupons.insertMany(coupons).insertedIds;
print(`Removed ${removed} old test coupons, inserted ${Object.keys(inserted).length}.`);
print(`Total coupons now: ${db.coupons.countDocuments()} (expired: ${db.coupons.countDocuments({ expiryDate: { $lt: new Date() } })})`);
