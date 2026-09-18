import { useEffect, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { NavGroup } from './NavGroup';

const PIN_KEY = 'tms-sidebar-pinned';

export function Sidebar() {
  const [pinned, setPinned] = useState(() => sessionStorage.getItem(PIN_KEY) === 'true');
  const [open, setOpen] = useState(() => sessionStorage.getItem(PIN_KEY) === 'true');

  useEffect(() => {
    sessionStorage.setItem(PIN_KEY, String(pinned));
    if (pinned) {
      setOpen(true);
    }
    document.documentElement.classList.toggle('sidebar-pinned-layout', pinned);
    return () => {
      document.documentElement.classList.remove('sidebar-pinned-layout');
    };
  }, [pinned]);

  function handleMouseLeave() {
    if (!pinned) {
      setOpen(false);
    }
  }

  function togglePin() {
    setPinned((prev) => {
      const next = !prev;
      if (next) {
        setOpen(true);
      }
      return next;
    });
  }

  return (
    <>
      {!open && !pinned && (
        <button
          type="button"
          className="sidebar-tab"
          onMouseEnter={() => setOpen(true)}
          onFocus={() => setOpen(true)}
          title="Open menu"
          aria-label="Open navigation menu"
        >
          <span className="sidebar-tab-label">Menu</span>
          <span className="sidebar-tab-chevron" aria-hidden="true">›</span>
        </button>
      )}
      <aside
        className={`sidebar ${pinned || open ? 'sidebar-open' : 'sidebar-collapsed'} ${pinned ? 'sidebar-pinned' : ''}`}
        onMouseLeave={handleMouseLeave}
        onMouseEnter={() => setOpen(true)}
      >
        <div className="sidebar-header">
          <span className="sidebar-title">Menu</span>
          <button
            type="button"
            className={`sidebar-pin ${pinned ? 'pinned' : ''}`}
            onClick={togglePin}
            title={pinned ? 'Unpin sidebar' : 'Pin sidebar'}
            aria-label={pinned ? 'Unpin sidebar' : 'Pin sidebar'}
            aria-pressed={pinned}
          >
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path
                d="M16 3v4.2l1.6 1.6-1.4 1.4L14 7.8V3h2zm-4 0v6.8L9.4 11.4 8 10l3-3V3h1zM7 3v8.2L3.8 14.4 5.2 16l5-5V3H7zm10 18H7v-2h10v2z"
                fill="currentColor"
              />
            </svg>
          </button>
        </div>

        <nav className="sidebar-nav">
          <NavLink
            to="/dashboard"
            className={({ isActive }) => (isActive ? 'nav-item nav-item-fixed active' : 'nav-item nav-item-fixed')}
          >
            Dashboard
          </NavLink>

          <NavGroup
            label="Tenders"
            matchPaths={['/tenders']}
            links={[
              { to: '/tenders', label: 'Tender List', end: true },
              { to: '/tenders/new', label: 'Create Tender' },
            ]}
          />

          <NavGroup
            label="Customers"
            matchPaths={['/customers']}
            links={[
              { to: '/customers', label: 'Customer List', end: true },
              { to: '/customers/new', label: 'Create Customer' },
            ]}
          />

          <NavGroup
            label="Admin"
            matchPaths={['/admin']}
            links={[
              { to: '/admin/users', label: 'Users' },
              { to: '/admin/roles', label: 'Roles' },
              { to: '/admin/settings', label: 'Settings' },
            ]}
          />
        </nav>
      </aside>
    </>
  );
}
