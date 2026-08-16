import { useState } from 'react';

export default function SearchBar({ initial = '', onSearch, placeholder = 'Tìm theo tên, tác giả, thể loại...' }) {
  const [value, setValue] = useState(initial);
  return (
    <form
      className="search-bar"
      onSubmit={(e) => { e.preventDefault(); onSearch(value.trim()); }}
    >
      <input
        type="text"
        placeholder={placeholder}
        value={value}
        onChange={(e) => setValue(e.target.value)}
      />
      <button type="submit">Tìm</button>
    </form>
  );
}
