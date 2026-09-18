import { useEffect, useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';

export interface NavLinkItem {
  to: string;
  label: string;
  end?: boolean;
}

interface NavGroupProps {
  label: string;
  links: NavLinkItem[];
  matchPaths: string[];
}

export function NavGroup({ label, links, matchPaths }: NavGroupProps) {
  const location = useLocation();
  const childActive = matchPaths.some((path) => location.pathname.startsWith(path));
  const [expanded, setExpanded] = useState(childActive);

  useEffect(() => {
    if (childActive) {
      setExpanded(true);
    }
  }, [childActive, location.pathname]);

  return (
    <div className={`nav-group ${expanded ? 'expanded' : ''}`}>
      <button
        type="button"
        className="nav-group-toggle"
        onClick={() => setExpanded((prev) => !prev)}
        aria-expanded={expanded}
      >
        <span>{label}</span>
        <span className="nav-chevron" aria-hidden="true">▾</span>
      </button>
      {expanded && (
        <div className="nav-group-items">
          {links.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.end}
              className={({ isActive }) => (isActive ? 'nav-sub-item active' : 'nav-sub-item')}
            >
              {link.label}
            </NavLink>
          ))}
        </div>
      )}
    </div>
  );
}
