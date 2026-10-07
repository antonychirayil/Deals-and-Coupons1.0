// Small helpers that decide how things LOOK. Plain functions: easy to reuse and to test.

// Material Symbols icon name for each category (https://fonts.google.com/icons)
const CATEGORY_ICONS: Record<string, string> = {
  Books: 'menu_book',
  Beauty: 'spa',
  Clothes: 'checkroom',
  Electronics: 'devices',
  Entertainment: 'movie',
  Fashion: 'checkroom',
  Food: 'restaurant',
  Groceries: 'shopping_basket',
  Health: 'health_and_safety',
  Home: 'chair',
  Kids: 'child_care',
  Services: 'home_repair_service',
  Sports: 'sports_soccer',
  Travel: 'flight',
};

export function categoryIcon(category: string): string {
  return CATEGORY_ICONS[category] ?? 'sell'; // ?? = "if missing, use this instead"
}

// A colour per store that never changes: the same name always gives the same colour
const AVATAR_COLORS = ['#7c3aed', '#2563eb', '#0891b2', '#059669', '#d97706', '#dc2626', '#db2777', '#4f46e5'];

export function storeColor(store: string): string {
  let hash = 0;
  for (const char of store) {
    hash = (hash * 31 + char.charCodeAt(0)) % 1000;
  }
  return AVATAR_COLORS[hash % AVATAR_COLORS.length];
}

// "Reliance Digital" -> "RD", "Amazon" -> "AM"
export function storeInitials(store: string): string {
  const words = store.trim().split(/\s+/);
  const initials = words.length > 1 ? words[0][0] + words[1][0] : store.slice(0, 2);
  return initials.toUpperCase();
}

// How many whole days from today until "2027-12-31"? Negative = in the past.
export function daysUntil(isoDate: string): number {
  const [year, month, day] = isoDate.split('-').map(Number);
  const target = new Date(year, month - 1, day); // local midnight, no timezone surprises
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  return Math.round((target.getTime() - today.getTime()) / 86_400_000); // ms in a day
}
