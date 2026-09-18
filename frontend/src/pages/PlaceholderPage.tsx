interface PlaceholderPageProps {
  title: string;
  description: string;
}

export function PlaceholderPage({ title, description }: PlaceholderPageProps) {
  return (
    <>
      <h2 className="page-title">{title}</h2>
      <p className="page-sub">{description}</p>
      <div className="detail-panel">
        <p>This screen is planned for a future phase. Navigation is wired for review.</p>
      </div>
    </>
  );
}
